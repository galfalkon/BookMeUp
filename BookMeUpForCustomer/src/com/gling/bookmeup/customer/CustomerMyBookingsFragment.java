package com.gling.bookmeup.customer;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardExpand;
import it.gmariotti.cardslib.library.internal.CardHeader;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.gling.bookmeup.main.Constants;
import com.gling.bookmeup.main.GenericMultiChoiceCardArrayAdapter;
import com.gling.bookmeup.main.ICardGenerator;
import com.gling.bookmeup.main.IObservableList;
import com.gling.bookmeup.main.ObservableArrayList;
import com.gling.bookmeup.main.OnClickListenerFragment;
import com.gling.bookmeup.main.PushUtils;
import com.gling.bookmeup.main.views.BaseListViewWrapperView.DisplayMode;
import com.gling.bookmeup.main.views.CardListViewWrapperView;
import com.gling.bookmeup.sharedlib.parse.Customer;
import com.gling.bookmeup.sharedlib.parse.ParseHelper;
import com.gling.bookmeup.sharedlib.parse.ParseHelper.Booking;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SendCallback;

public class CustomerMyBookingsFragment extends OnClickListenerFragment {
	private static final String TAG = "CustomerMyBookingsFragment";
	
	IObservableList<Booking> _bookings;
	GenericMultiChoiceCardArrayAdapter<Booking> _bookingsCardAdapter;
	CardListViewWrapperView _bookingsListViewWrapperView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View view = super.onCreateView(inflater, container, savedInstanceState);
		
		_bookings = new ObservableArrayList<Booking>();
		_bookingsCardAdapter = new BookingMultiChoiceCardArrayAdapter(_bookings, new BookingCardGenerator(), R.menu.customer_my_bookings_mutlichoice);
		
		_bookingsListViewWrapperView = (CardListViewWrapperView) view.findViewById(R.id.customer_my_booking_listViewWrapper);
		_bookingsListViewWrapperView.setAdapter(_bookingsCardAdapter);
		_bookingsListViewWrapperView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

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
				if (e != null)
				{
					Log.e(TAG, "Exception: " + e.getMessage());
					return;
				}
				
				for (Booking booking : retrievedBookings)
				{
					_bookings.add(booking);
				}
				
				DisplayMode newDisplayMode = _bookings.isEmpty()? DisplayMode.NO_ITEMS_VIEW : DisplayMode.LIST_VIEW;
				_bookingsListViewWrapperView.setDisplayMode(newDisplayMode);
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
	
	private void handleBookingsCancellation(List<Booking> bookingsToCancel) 
	{
		Log.i(TAG, "handleBookingsCancellation");
		
		for (Booking booking : bookingsToCancel)
		{
			booking.setStatus(Booking.Status.CANCELED);
			booking.saveInBackground();
			
			PushUtils.notifyBusinessAboutBookingCancellation(booking.getBusiness().getObjectId(), booking.getCustomer().getName(), new SendCallback() 
			{
				@Override
				public void done(ParseException e) 
				{
					Log.i(TAG, "notifyBusinessAboutBookingCancellation done");
					if (e != null)
					{
						Log.e(TAG, "Exception: " + e.getMessage());
					}
				}
			});

			_bookingsCardAdapter.refreshItem(_bookings.indexOf(booking));
		}
		
		_bookingsCardAdapter.notifyDataSetChanged();
	}
	
	private class BookingCardGenerator implements ICardGenerator<Booking>
	{
		@Override
		public Card generateCard(Booking booking) 
		{
			return new BookingCard(getActivity(), booking);
		}
	}
	
	private class BookingMultiChoiceCardArrayAdapter extends GenericMultiChoiceCardArrayAdapter<Booking>
	{
		public BookingMultiChoiceCardArrayAdapter(IObservableList<Booking> items, ICardGenerator<Booking> cardFactory, int menuRes) 
		{
			super(getActivity(), items, cardFactory, menuRes);
		}
		
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) 
		{
			switch (item.getItemId())
        	{
        	case R.id.customer_my_bookings_multichoice_cancel:
        		handleBookingsCancellation(getSelectedItems());
        		mode.finish();
        		return true;
        	}
        	
        	return false;
		}
	}
	
	private class BookingCard extends Card
    {
		private final Context _context;
    	private final Booking _booking; 
    	
		public BookingCard(final Context context, Booking booking) 
		{
			super(context, R.layout.customer_my_bookings_booking_card);
			
			_context = context;
			_booking = booking;
			
			CardHeader cardHeader = new CardHeader(context);
			cardHeader.setTitle(booking.getBusiness().getName());
			cardHeader.setButtonExpandVisible(true);
	    	
			CardExpand expand = new CardExpand(context);
			expand.setTitle("Last updated: " + Constants.DATE_TIME_FORMAT.format(booking.getUpdatedAt()));
			
	    	setTitle(_booking.getBusiness().getName());
	    	addCardHeader(cardHeader);
	    	addCardExpand(expand);
			setOnClickListener(new OnCardClickListener() 
			{
				@Override
				public void onClick(Card card, View view) 
				{
					doToogleExpand();
				}
			});
			
			if (_booking.getStatus() != Booking.Status.CANCELED)
			{
				setOnLongClickListener(new OnLongCardClickListener() 
				{
					@Override
					public boolean onLongClick(Card card, View view) 
					{
						return _bookingsCardAdapter.startActionMode(getActivity());
					}
				});
			}
		}
		
		@Override
		public void setupInnerViewElements(ViewGroup parent, View view) 
		{
			TextView txtService = (TextView) view.findViewById(R.id.customer_my_bookings_booking_card_service);
			txtService.setText(_booking.getServiceName());
			
			TextView txtDate = (TextView) view.findViewById(R.id.customer_my_bookings_booking_card_date);
			txtDate.setText(Constants.DATE_TIME_FORMAT.format(_booking.getDate()));
			
			TextView txtStatus = (TextView) view.findViewById(R.id.customer_my_bookings_booking_card_status);
			Resources resources = getContext().getResources();
			int statusStringResourceId, statusColorResourceId;
			switch (_booking.getStatus())
			{
			case Booking.Status.PENDING:
				statusStringResourceId = R.string.customer_my_bookings_booking_pending_for_approval;
				statusColorResourceId = android.R.color.holo_purple;
				break;
			case Booking.Status.APPROVED:
				statusStringResourceId = R.string.customer_my_bookings_booking_approved;
				statusColorResourceId = android.R.color.holo_green_light;
				break;
			case Booking.Status.CANCELED:
			default:
				statusStringResourceId = R.string.customer_my_bookings_booking_canceled;
				statusColorResourceId = android.R.color.holo_red_light;
				break;
			}
			
			txtStatus.setText(resources.getString(statusStringResourceId));
			txtStatus.setTextColor(resources.getColor(statusColorResourceId));
		}
    }
}