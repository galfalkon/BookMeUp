package com.gling.bookmeup.business;

import it.gmariotti.cardslib.library.internal.Card;

import java.util.List;

import org.joda.time.DateTime;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gling.bookmeup.main.Constants;
import com.gling.bookmeup.main.GenericCardArrayAdapter;
import com.gling.bookmeup.main.ICardGenerator;
import com.gling.bookmeup.main.IObservableList;
import com.gling.bookmeup.main.ObservableArrayList;
import com.gling.bookmeup.main.views.BaseListViewWrapperView.DisplayMode;
import com.gling.bookmeup.main.views.CardListViewWrapperView;
import com.gling.bookmeup.sharedlib.parse.Business;
import com.gling.bookmeup.sharedlib.parse.ParseHelper.Booking;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

public class BusinessCalendarFragment extends Fragment {

    private static final String TAG = "BusinessCalendarFragment";
    private static final String ARG_DATE = "business_calendar_fragment_date";

    private DateTime _date;

    private IObservableList<Booking> _bookings;
    private GenericCardArrayAdapter<Booking> _bookingsCardAdapter;
    private CardListViewWrapperView _bookingsListViewWrapperView;
    
    /**
     * Returns a new instance of this fragment for the given date.
     */
    public static BusinessCalendarFragment newInstance(DateTime dateTime) {
        BusinessCalendarFragment fragment = new BusinessCalendarFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, dateTime);
        fragment.setArguments(args);
        return fragment;
    }

    public BusinessCalendarFragment() {

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

		ParseQuery<Booking> query = new ParseQuery<Booking>(Booking.CLASS_NAME);
        query.whereEqualTo(Booking.Keys.BUSINESS_POINTER, Business.getCurrentBusiness());
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
			
			return new BusinessCalendarBookingCard(
					getActivity(), 
					booking.getCustomer().getName(),
					booking.getServiceName(), 
					Constants.TIME_FORMAT.format(booking.getDate()), 
					status, 
					statusColor,
					"Last updated: " + Constants.DATE_FORMAT.format(booking.getUpdatedAt()));
		}
	}

}