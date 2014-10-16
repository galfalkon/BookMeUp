package com.gling.bookmeup.customer;

import it.gmariotti.cardslib.library.internal.Card;

import java.util.List;

import org.joda.time.DateTime;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gling.bookmeup.R;
import com.gling.bookmeup.business.Business;
import com.gling.bookmeup.main.Constants;
import com.gling.bookmeup.main.GenericCardArrayAdapter;
import com.gling.bookmeup.main.ICardGenerator;
import com.gling.bookmeup.main.IObservableList;
import com.gling.bookmeup.main.ObservableArrayList;
import com.gling.bookmeup.main.ParseHelper.Booking;
import com.gling.bookmeup.main.views.BaseListViewWrapperView.DisplayMode;
import com.gling.bookmeup.main.views.CardListViewWrapperView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

public class CustomerCalendarFragment extends Fragment {

	private static final String TAG = "CustomerCalendarFragment";
	private static final String ARG_DATE = "customer_calendar_fragment_date";

	private DateTime _date;

	IObservableList<Booking> _bookings;
	GenericCardArrayAdapter<Booking> _bookingsCardAdapter;
	CardListViewWrapperView _bookingsListViewWrapperView;

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
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.business_calendar_fragment, container, false);

		_bookings = new ObservableArrayList<Booking>();
		_bookingsCardAdapter = new GenericCardArrayAdapter<Booking>(getActivity(), _bookings, new BookingCardGenerator());

		_bookingsListViewWrapperView = (CardListViewWrapperView) view.findViewById(R.id.business_calendar_cardListViewWrapper);
		_bookingsListViewWrapperView.setAdapter(_bookingsCardAdapter);

		FragmentActivity activity = getActivity();
		if (activity instanceof CustomerCalendarActivity) {
			CustomerCalendarActivity calendarActivity = (CustomerCalendarActivity)activity;
			String businessId = calendarActivity.getStringExtra(CustomerCalendarActivity.BUSINESS_ID_EXTRA);
			if (businessId != null) {
				ParseQuery<Business> businessQuery = new ParseQuery<Business>(Business.CLASS_NAME);
				businessQuery.whereEqualTo(Business.Keys.ID, businessId);

				_bookingsListViewWrapperView.setDisplayMode(DisplayMode.LOADING_VIEW);
				businessQuery.findInBackground(new FindCallback<Business>() {
					@Override
					public void done(List<Business> objects, ParseException e) {
						if (objects.size() != 1) {
							Log.e(TAG, "problem");
							return;
						}
						Business business = objects.get(0);

						ParseQuery<Booking> query = new ParseQuery<Booking>(Booking.CLASS_NAME);
						query.whereEqualTo(Booking.Keys.BUSINESS_POINTER, business);
						query.whereGreaterThanOrEqualTo(Booking.Keys.DATE, _date.toDate());
						query.whereLessThan(Booking.Keys.DATE, _date.plusDays(1).toDate());
						query.orderByAscending(Booking.Keys.DATE);
						query.include(Booking.Keys.CUSTOMER_POINTER);
						query.include(Booking.Keys.SERVICE_POINTER);

						_bookingsListViewWrapperView.setDisplayMode(DisplayMode.LOADING_VIEW);
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
									_bookings.add(booking);
								}

								DisplayMode newDisplayMode = _bookings.isEmpty() ? DisplayMode.NO_ITEMS_VIEW : DisplayMode.LIST_VIEW;
								_bookingsListViewWrapperView.setDisplayMode(newDisplayMode);
							}
						});
					}
				});
			}
		}


		return view;
	}

	private class BookingCardGenerator implements ICardGenerator<Booking>
	{
		@Override
		public Card generateCard(Booking booking) 
		{
			String status;
			int statusColor;
			switch (booking.getStatus())
			{
			case Booking.Status.PENDING:
				status = getString(R.string.customer_my_bookings_booking_pending_for_approval);
				statusColor = getResources().getColor(android.R.color.holo_purple);
				break;
			case Booking.Status.APPROVED:
				status = getString(R.string.customer_my_bookings_booking_approved);
				statusColor = getResources().getColor(android.R.color.holo_green_light);
				break;
			case Booking.Status.CANCELED:
			default:
				status = getString(R.string.customer_my_bookings_booking_canceled);
				statusColor = getResources().getColor(android.R.color.holo_red_light);
				break;
			}

			return new CustomerCalendarBookingCard(
					getActivity(), 
					booking.getCustomerName(),
					booking.getServiceName(), 
					Constants.TIME_FORMAT.format(booking.getDate()), 
					status, 
					statusColor,
					"Last updated: " + Constants.DATE_FORMAT.format(booking.getUpdatedAt()));
		}
	}

}