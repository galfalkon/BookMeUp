package com.gling.bookmeup.customer;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gling.bookmeup.R;
import com.gling.bookmeup.main.GenericCardArrayAdapter;
import com.gling.bookmeup.main.ICardGenerator;
import com.gling.bookmeup.main.IObservableList;
import com.gling.bookmeup.main.ObservableArrayList;
import com.gling.bookmeup.main.OnClickListenerFragment;
import com.gling.bookmeup.main.ParseHelper;
import com.gling.bookmeup.main.ParseHelper.Booking;
import com.gling.bookmeup.main.views.CardListViewWrapperView;
import com.gling.bookmeup.main.views.BaseListViewWrapperView.DisplayMode;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

public class CustomerMyBookingsFragment extends OnClickListenerFragment {
	private static final String TAG = "CustomerMyBookingsFragment";
	
	IObservableList<Booking> _bookings;
	GenericCardArrayAdapter<Booking> _bookingsCardAdapter;
	CardListViewWrapperView _bookingsListViewWrapperView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View view = super.onCreateView(inflater, container, savedInstanceState);
		
		_bookings = new ObservableArrayList<Booking>();
		_bookingsCardAdapter = new GenericCardArrayAdapter<Booking>(getActivity(), _bookings, new BookingCardGenerator());
		
		_bookingsListViewWrapperView = (CardListViewWrapperView) view.findViewById(R.id.customer_my_booking_listViewWrapper);
		_bookingsListViewWrapperView.setAdapter(_bookingsCardAdapter);

		ParseQuery<Booking> bookingsQuery = new ParseQuery<Booking>(Booking.CLASS_NAME)
				.whereEqualTo(Booking.Keys.CUSTOMER_POINTER, Customer.getCurrentCustomer())
				.whereGreaterThan(Booking.Keys.DATE, new Date())
				.orderByDescending(Booking.Keys.DATE);
		bookingsQuery.include(Booking.Keys.CUSTOMER_POINTER);
		bookingsQuery.include(Booking.Keys.BUSINESS_POINTER);
		bookingsQuery.include(Booking.Keys.SERVICE_POINTER);
		
		_bookingsListViewWrapperView.setDisplayMode(DisplayMode.LOADING_VIEW);
		bookingsQuery.findInBackground(new FindCallback<ParseHelper.Booking>() 
		{
			@Override
			public void done(List<Booking> retrievedBookings, ParseException e) 
			{
				Log.i(TAG, "bookingsQuery.findInBackground done");
				_bookingsListViewWrapperView.setDisplayMode(DisplayMode.LIST_VIEW);
				if (e != null)
				{
					Log.e(TAG, "Exception: " + e.getMessage());
					return;
				}
				
				for (Booking booking : retrievedBookings)
				{
					if (booking.getStatus() == Booking.Status.CANCELED)
					{
						continue;
					}
					_bookings.add(booking);
				}
			}
		});
		
		return view;
	}
	
	@Override
	public void onClick(View view) 
	{
	}

	@Override
	protected int getFragmentLayoutId() 
	{
		return R.layout.customer_my_bookings_fragment;
	}
	
	private class BookingCardGenerator implements ICardGenerator<Booking>
	{
		@Override
		public Card generateCard(Booking booking) 
		{
			CardHeader cardHeader = new CardHeader(getActivity());
			cardHeader.setTitle(booking.getBusinessName());
	    	
			String cardTitle = 
					"Service: " + booking.getServiceName() + "\n" +
							"Date: " + (new SimpleDateFormat("dd-MM-yy")).format(booking.getDate()) + "\n";
			String status;
			int statusColor;
			switch (booking.getStatus())
			{
			case Booking.Status.PENDING:
				status = getString(R.string.customer_my_bookings_booking_pending_for_approval);
				statusColor = getResources().getColor(android.R.color.holo_purple);
				break;
			case Booking.Status.APPROVED:
			default:
				status = getString(R.string.customer_my_bookings_booking_approved);
				statusColor = getResources().getColor(android.R.color.holo_green_light);
				break;
			}
	    	Card card = new BookingCard(getActivity(), cardTitle, status, statusColor); 
	    	card.addCardHeader(cardHeader);
			card.setTitle(cardTitle);

	    	return card;
		}
	}
}