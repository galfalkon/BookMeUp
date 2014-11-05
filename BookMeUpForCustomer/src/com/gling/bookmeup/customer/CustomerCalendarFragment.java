package com.gling.bookmeup.customer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gling.bookmeup.main.Constants;
import com.gling.bookmeup.main.PushUtils;
import com.gling.bookmeup.main.views.BaseListViewWrapperView.DisplayMode;
import com.gling.bookmeup.main.views.NormalListViewWrapperView;
import com.gling.bookmeup.sharedlib.parse.Business;
import com.gling.bookmeup.sharedlib.parse.Customer;
import com.gling.bookmeup.sharedlib.parse.ParseHelper.Booking;
import com.gling.bookmeup.sharedlib.parse.Service;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SendCallback;

public class CustomerCalendarFragment extends Fragment {

	private static final String TAG = "CustomerCalendarFragment";
	private static final String ARG_DATE = "customer_calendar_fragment_date";

	private DateTime _date;
	private TextView _headerView;

	String _businessId = null;
	String _serviceId = null;
	Integer _offerDiscount = null;
	Date _offerExpirationDate = null;

	ArrayList<Booking> _alreadyBooked;
	ArrayList<Booking> _possibleBookings;
	ArrayList<DatedBooking> _datedBooked;
	CustomerCalendarAdapter _bookingsCardAdapter;
	NormalListViewWrapperView _bookingsListViewWrapperView;

	/**
	 * Returns a new instance of this fragment for the given date.
	 */
	public static CustomerCalendarFragment newInstance(DateTime dateTime) {
		CustomerCalendarFragment fragment = new CustomerCalendarFragment();
		Bundle args = new Bundle();
		args.putSerializable(ARG_DATE, dateTime);
		fragment.setArguments(args);
		return fragment;
	}

	public CustomerCalendarFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);

		_date = (DateTime) getArguments().getSerializable(ARG_DATE);
		FragmentActivity activity = getActivity();
		if (activity instanceof CustomerCalendarActivity) {
			CustomerCalendarActivity calendarActivity = (CustomerCalendarActivity)activity;
			_businessId = calendarActivity.getStringExtra(CustomerCalendarActivity.BUSINESS_ID_EXTRA);
			_serviceId = calendarActivity.getStringExtra(CustomerCalendarActivity.SERVICE_ID_EXTRA);
			Bundle extras = getActivity().getIntent().getExtras();
			if (extras != null) {
				_offerDiscount = extras.getInt(CustomerCalendarActivity.OFFER_DISCOUNT_EXTRA);
				Serializable serializable = extras.getSerializable(CustomerCalendarActivity.OFFER_EXPIRATION_EXTRA);
				if (serializable instanceof Date) {
					_offerExpirationDate = (Date) serializable;
				}
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.customer_calendar_fragment, container, false);

		_datedBooked = new ArrayList<CustomerCalendarFragment.DatedBooking>();

		_alreadyBooked = new ArrayList<Booking>();
		_possibleBookings = new ArrayList<Booking>();
		_bookingsCardAdapter = new CustomerCalendarAdapter(getActivity(), R.layout.customer_calendar_fragment_list_item, _possibleBookings);
		_bookingsListViewWrapperView = (NormalListViewWrapperView) view.findViewById(R.id.customer_calendar_cardListViewWrapper);
		
		_headerView = new TextView(getActivity().getApplicationContext());
		_headerView.setGravity(Gravity.CENTER);
		_headerView.setTextSize(18);
		_headerView.setTextColor(Color.BLACK);
		_bookingsListViewWrapperView.getListView().addHeaderView(_headerView);
		
		_bookingsListViewWrapperView.setAdapter(_bookingsCardAdapter);

		final Customer currentCustomer = Customer.getCurrentCustomer();
		if (_businessId != null) {
			ParseQuery<Business> businessQuery = new ParseQuery<Business>(Business.CLASS_NAME);
			businessQuery.whereEqualTo(Business.Keys.ID, _businessId);

			_bookingsListViewWrapperView.setDisplayMode(DisplayMode.LOADING_VIEW);
			_bookingsListViewWrapperView.getListView().setDividerHeight(0);
			_bookingsListViewWrapperView.getListView().setDivider(null);
			businessQuery.findInBackground(new FindCallback<Business>() {
				@Override
				public void done(List<Business> objects, ParseException e) {
					if ((objects == null) || (objects.size() != 1)) {
						Log.e(TAG, "problem");
						return;
					}
					final Business business = objects.get(0);

					ParseQuery<Booking> query = new ParseQuery<Booking>(Booking.CLASS_NAME);
					query.whereEqualTo(Booking.Keys.BUSINESS_POINTER, business);
					query.whereGreaterThanOrEqualTo(Booking.Keys.DATE, _date.toDate());
					query.whereLessThan(Booking.Keys.DATE, _date.plusDays(1).toDate());
					query.orderByAscending(Booking.Keys.DATE);
					//						query.include(Booking.Keys.CUSTOMER_POINTER);
					query.include(Booking.Keys.SERVICE_POINTER);

					//						_bookingsListViewWrapperView.setDisplayMode(DisplayMode.LOADING_VIEW);
					query.findInBackground(new FindCallback<Booking>() 
							{
						@Override
						public void done(List<Booking> retrievedBookings, ParseException e) 
						{
							Log.i(TAG, "bookingsQuery.findInBackground done");
							if (e != null)
							{
								Log.e(TAG, "Exception: " + e.getMessage());
								return;
							}

							for (Booking booking : retrievedBookings)
							{
								_alreadyBooked.add(booking);
								Date startDate = booking.getDate();
								DateTime startTime = new DateTime(startDate);
								int duration = booking.getServiceDuration();
								DateTime endTime = startTime.plusMinutes(duration);
								Date endDate = endTime.toDate();
								_datedBooked.add(new DatedBooking(startDate, endDate));
							}

							ParseQuery<Service> serviceQuery = new ParseQuery<Service>(Service.CLASS_NAME);
							serviceQuery.whereEqualTo(Business.Keys.ID, _serviceId);
							serviceQuery.findInBackground(new FindCallback<Service>() {
								@Override
								public void done(List<Service> objects, ParseException e) {

									if (objects.size() != 1) {
										Log.e(TAG, "problem");
										return;
									}
									Service service = objects.get(0);
									int duration = service.getDuration();
									_bookingsCardAdapter.setDurtation(duration);
									String serviceName = service.getName();
									String headerText = getActivity().getApplicationContext().getString(R.string.customer_calendar_choosing_instructions) +
											" " + serviceName + " service?";
									_headerView.setText(headerText);

									DateTime startHour = _date.plusHours(8);
									DateTime currentDate = new DateTime();
									if ((currentDate.getYear() == _date.getYear()) &&
											(currentDate.getMonthOfYear() == _date.getMonthOfYear()) &&
											(currentDate.getDayOfMonth() == _date.getDayOfMonth()) &&
											(currentDate.getHourOfDay() > 8)) {
										//fragment is at current date
										DateTime nextRoundHour = new DateTime(currentDate.getYear(), currentDate.getMonthOfYear(),
												currentDate.getDayOfMonth(), currentDate.getHourOfDay() + 1, 0);
										startHour = nextRoundHour;
									}
									DateTime endHour = startHour.plusMinutes(duration);
									while (endHour.getHourOfDay() < 20) {
										boolean conflicted = false;
										for (DatedBooking booked : _datedBooked) {
											if ((startHour.toDate().before(booked.getStartDate()) &&
													endHour.toDate().after(booked.getStartDate())) || 
													(startHour.toDate().before(booked.getEndDate()) &&
															startHour.toDate().after(booked.getStartDate()))) {
												Log.i(TAG, startHour.toDate().toString() + " is conflicted");
												conflicted = true;
											}
										}

										if (!conflicted) {
											//create new booking
											Booking booking = new Booking();
											booking.setBusiness(business);
											booking.setCustomer(currentCustomer);
											booking.setDate(startHour.toDate());
											booking.setService(service);
											booking.setStatus(Booking.Status.PENDING);

											//add to _possibleBookings
											_possibleBookings.add(booking);
											_bookingsCardAdapter.notifyDataSetChanged();
										}

										startHour = endHour;
										endHour = startHour.plusMinutes(duration);
									}


									DisplayMode newDisplayMode = _possibleBookings.isEmpty() ? DisplayMode.NO_ITEMS_VIEW : DisplayMode.LIST_VIEW;
									_bookingsListViewWrapperView.setDisplayMode(newDisplayMode);
								}
							});


						}
							});
				}
			});
		}
		//		}

		_bookingsListViewWrapperView.getListView().setOnItemClickListener( new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle(R.string.customer_booking_title);
				final Booking bookingToSave = _possibleBookings.get(position);
				builder.setMessage("For service:   " + bookingToSave.getServiceName() +
						"\nAt time:          " + Constants.DATE_TIME_FORMAT.format(bookingToSave.getDate()));

				// Set up the buttons
				builder.setPositiveButton(R.string.customer_booking_ok_button, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Log.i(TAG, "Sending booking to parse & business");
						try {
							bookingToSave.save();
							SendCallback callback = new SendCallback() {

								@Override
								public void done(ParseException e) {
									Intent intent = new Intent(getActivity(), CustomerMainActivity.class);
									intent.putExtra(CustomerMainActivity.GO_TO_BOOKING_EXTRA, true);
									startActivity(intent);
								}
							};
							PushUtils.notifyBusinessAboutBookingRequest(bookingToSave.getBusiness().getObjectId(), 
									bookingToSave.getCustomer().getName(), callback);
						} catch (ParseException e1) {
							Log.e(TAG, "Problem saving the booking");
						}

					}
				});
				builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
				builder.show();
			}

		});


		return view;
	}

	private class DatedBooking {
		private Date _startDate = null;
		private Date _endDate = null;

		public DatedBooking(Date startDate, Date endDate) {
			_startDate = startDate;
			_endDate = endDate;
		}

		public Date getStartDate() {
			return _startDate;
		}

		public Date getEndDate() {
			return _endDate;
		}

	}

	private class CustomerCalendarAdapter extends ArrayAdapter<Booking> {

		List<Booking> _bookings ;
		Integer _duration = null; 

		public CustomerCalendarAdapter(Context context, int textViewResourceId,
				List<Booking> objects) {
			super(context, textViewResourceId, objects);
			_bookings = objects;
		}

		public void setDurtation(int duration) {
			_duration = duration;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) getActivity()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.customer_calendar_fragment_list_item, parent, false);
			TextView startHourTextView = (TextView) rowView.findViewById(R.id.customer_calendar_list_item_txtHour);
			Booking booking = _bookings.get(position);
			Date startDate = booking.getDate();
			String startTime = Constants.TIME_FORMAT.format(startDate);
			startHourTextView.setText(startTime);
			if (_duration != null) {
//								CardView durationCard = ((CardView) rowView.findViewById(R.id.customer_calendar_list_item_txtDuration));
				TextView DurationTextView = (TextView) rowView.findViewById(R.id.customer_calendar_list_item_txtDuration);
				DateTime startDateTime = new DateTime(startDate);
				DateTime endDate = startDateTime.plusMinutes(_duration);
				String endTime = Constants.TIME_FORMAT.format(endDate.toDate());
//								durationCard.setCard(new Card(getActivity()));
				DurationTextView.setText(startTime + " - " + endTime);
				int color = getActivity().getApplicationContext().getResources().getColor(R.color.bookmeup_widgets_color);
				DurationTextView.setTextColor(color);
			}

			return rowView;
		}
	}

}