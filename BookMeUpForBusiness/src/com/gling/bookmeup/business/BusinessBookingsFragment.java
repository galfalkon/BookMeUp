package com.gling.bookmeup.business;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.Card.OnLongCardClickListener;
import it.gmariotti.cardslib.library.internal.CardHeader.OnClickCardHeaderOtherButtonListener;
import it.gmariotti.cardslib.library.internal.CardHeader;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.gling.bookmeup.main.Constants;
import com.gling.bookmeup.main.GenericMultiChoiceCardArrayAdapter;
import com.gling.bookmeup.main.ICardGenerator;
import com.gling.bookmeup.main.IObservableList;
import com.gling.bookmeup.main.ObservableArrayList;
import com.gling.bookmeup.main.OnClickListenerFragment;
import com.gling.bookmeup.main.PushUtils;
import com.gling.bookmeup.main.views.BaseListViewWrapperView.DisplayMode;
import com.gling.bookmeup.main.views.CardListViewWrapperView;
import com.gling.bookmeup.sharedlib.parse.Business;
import com.gling.bookmeup.sharedlib.parse.ParseHelper.Booking;
import com.gling.bookmeup.sharedlib.parse.ParseHelper.Booking.Status;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SendCallback;

public class BusinessBookingsFragment extends OnClickListenerFragment {

    private static final String TAG = "BusinessBookingsFragment";
    
    private IObservableList<Booking> _pendingBookings, _approvedBookings;
    private GenericMultiChoiceCardArrayAdapter<Booking> _pendingBookingsAdapter, _approvedBookingsAdapter;

	private Button _btnPending, _btnApproved;
	private ViewSwitcher _viewSwitcher;
	
	private CardListViewWrapperView _pendingBookingsListView, _approvedBookingsListView;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        _pendingBookings = new ObservableArrayList<Booking>();
        _approvedBookings = new ObservableArrayList<Booking>();
        _pendingBookingsAdapter = new GenericMultiChoiceCardArrayAdapter<Booking>(getActivity(), _pendingBookings, new PendingBookingCardGenerator(), R.menu.business_booking_list_pending_mutlichoice)
		{
			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) 
			{
				switch (item.getItemId())
				{
				case R.id.business_booking_list_pending_mutlichoice_action_bar_menu_approve:
					handleApprovalOfSelectedBookings();
					break;
				}
				
				mode.finish();
				return false;
			}
		};
        _approvedBookingsAdapter = new GenericMultiChoiceCardArrayAdapter<Booking>(getActivity(), _approvedBookings, new ApprovedBookingCardGenerator(), R.menu.business_booking_list_approved_mutlichoice)
		{
			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) 
			{
				switch (item.getItemId())
				{
				case R.id.business_booking_list_approved_mutlichoice_action_bar_menu_cancel:
					handleCancellationOfSelectedBookings();
					break;
				}
				
				mode.finish();
				return false;
			}
		};
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        
        _btnPending = (Button)view.findViewById(R.id.business_bookings_btnPending);
        _btnApproved = (Button)view.findViewById(R.id.business_bookings_btnApproved);
        
        _pendingBookingsListView = (CardListViewWrapperView)view.findViewById(R.id.business_bookings_cardListViewPendingBookings);
        _pendingBookingsListView.setAdapter(_pendingBookingsAdapter);
        _pendingBookingsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        
        _approvedBookingsListView = (CardListViewWrapperView)view.findViewById(R.id.business_bookings_cardListViewApprovedBookings);
        _approvedBookingsListView.setAdapter(_approvedBookingsAdapter);
        _approvedBookingsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        
        _viewSwitcher = (ViewSwitcher)view.findViewById(R.id.business_bookings_viewSwitcher); 
        
        inflateListWithFutureBookings();
        
        return view;
    }

    @Override
    public void onClick(View v) {
    	switch (v.getId())
    	{
    	case R.id.business_bookings_btnPending:
    		Log.i(TAG, "btnPending clicked");
    		showPendingBookings();
    		break;
    	case R.id.business_bookings_btnApproved:
    		Log.i(TAG, "btnApproved clicked");
    		showApprovedBookings();
    		break;
    	}
    }

    @Override
    protected int getFragmentLayoutId() {
        return R.layout.business_bookings_fragment;
    }

    private void inflateListWithFutureBookings() {
        ParseQuery<Booking> query = new ParseQuery<Booking>(Booking.CLASS_NAME).
                whereEqualTo(Booking.Keys.BUSINESS_POINTER, Business.getCurrentBusiness()).
                whereGreaterThan(Booking.Keys.DATE, new Date());
        query.include(Booking.Keys.BUSINESS_POINTER);
        query.include(Booking.Keys.CUSTOMER_POINTER);
        query.include(Booking.Keys.SERVICE_POINTER);

        _pendingBookingsListView.setDisplayMode(DisplayMode.LOADING_VIEW);
        _approvedBookingsListView.setDisplayMode(DisplayMode.LOADING_VIEW);
        query.findInBackground(new FindCallback<Booking>() {
            @Override
            public void done(List<Booking> objects, ParseException e) {
                Log.i(TAG, "Done querying future bookings. #objects = " + objects.size());
                if (e != null) {
                    Log.e(TAG, "Exception occurred: " + e.getMessage());
                    return;
                }
                
                for (Booking booking : objects)
                {
                	switch (booking.getStatus())
                	{
                	case Booking.Status.PENDING:
                		_pendingBookings.add(booking);
                		break;
                	case Booking.Status.APPROVED:
                		_approvedBookings.add(booking);
                	}
                }
                
                updatePendingBookingsTitleAndDisplayMode();
                updateApprovedBookingsTitleAndDisplayMode();
                _pendingBookingsAdapter.notifyDataSetChanged();
                _approvedBookingsAdapter.notifyDataSetChanged();
            }
        });
    }
    
    private void showPendingBookings()
    {
    	if (_viewSwitcher.getDisplayedChild() != 0)
    	{
    		_viewSwitcher.setDisplayedChild(0);
    	}
    }
    
    private void showApprovedBookings()
    {
    	if (_viewSwitcher.getDisplayedChild() != 1)
    	{
    		_viewSwitcher.setDisplayedChild(1);
    	}
    }
    
    private void updatePendingBookingsTitleAndDisplayMode()
    {
    	// Update title
    	if (isAdded()) {
    		_btnPending.setText(String.format("%s (%d)",getString(R.string.business_bookings_list_pending_header), _pendingBookings.size()));
    	}
    	
    	
    	// Update display mode
    	DisplayMode newDisplayMode = _pendingBookings.isEmpty()? DisplayMode.NO_ITEMS_VIEW : DisplayMode.LIST_VIEW;
    	_pendingBookingsListView.setDisplayMode(newDisplayMode);
    }
    
    private void updateApprovedBookingsTitleAndDisplayMode()
    {
    	// Update title
    	if (isAdded()) {
    		_btnApproved.setText(String.format("%s (%d)",getString(R.string.business_bookings_list_approved_header), _approvedBookings.size()));
    	}
    	
    	// Update display mode
    	DisplayMode newDisplayMode = _approvedBookings.isEmpty()? DisplayMode.NO_ITEMS_VIEW : DisplayMode.LIST_VIEW;
    	_approvedBookingsListView.setDisplayMode(newDisplayMode);
    }
    
    private void handleApprovalOfSelectedBookings()
    {
    	Log.i(TAG, "handleApprovalOfSelectedBookings");
    	List<Booking> selectedBookings = _pendingBookingsAdapter.getSelectedItems();
    	for (Booking booking : selectedBookings)
    	{
    		booking.setStatus(Status.APPROVED);
            booking.saveInBackground();
            
            _pendingBookings.remove(booking);
            _approvedBookings.add(booking);
            updatePendingBookingsTitleAndDisplayMode();
            updateApprovedBookingsTitleAndDisplayMode();
            
            PushUtils.notifyCustomerAboutApprovedBooking(booking, new SendCallback() {
				@Override
				public void done(ParseException e) {
					Log.i(TAG, "notifyCustomerAboutApprovedBooking done");
					if (e != null)
					{
						Log.e(TAG, "Exception: " + e.getMessage());
						return;
					}
				}
			});
    	}
    }
    
    private void handleCancellationOfSelectedBookings()
    {
    	Log.i(TAG, "handleCancellationOfSelectedBookings");
    	List<Booking> selectedBookings = _approvedBookingsAdapter.getSelectedItems();
    	for (Booking booking : selectedBookings)
    	{
    		booking.setStatus(Status.CANCELED);
            booking.saveInBackground();
            
            _approvedBookings.remove(booking);
            updateApprovedBookingsTitleAndDisplayMode();
            
            PushUtils.notifyCustomerAboutCanceledBooking(booking, new SendCallback() {
				@Override
				public void done(ParseException e) {
					Log.i(TAG, "notifyCustomerAboutCanceledBooking done");
					if (e != null)
					{
						Log.e(TAG, "Exception: " + e.getMessage());
						return;
					}
				}
			});
    	}
    }
    
    private class PendingBookingCardGenerator implements ICardGenerator<Booking>
    {
    	@Override
		public Card generateCard(final Booking booking) 
		{
			return new BookingCard(getActivity(), booking, new OnLongCardClickListener() 
			{
				@Override
				public boolean onLongClick(Card card, View view) 
				{
					return _pendingBookingsAdapter.startActionMode(getActivity());
				}
			});
		}
    }
    
    private class ApprovedBookingCardGenerator implements ICardGenerator<Booking>
    {
		@Override
		public Card generateCard(final Booking booking) 
		{
			return new BookingCard(getActivity(), booking, new OnLongCardClickListener() 
			{
				@Override
				public boolean onLongClick(Card card, View view) 
				{
					return _approvedBookingsAdapter.startActionMode(getActivity());
				}
			});
		}
    }
    
    private static class BookingCard extends Card
    {
    	private final Booking _booking; 
    	
		public BookingCard(final Context context, final Booking booking, OnLongCardClickListener onLongClickListener) 
		{
			super(context, R.layout.business_booking_list_booking_card);
			
			_booking = booking;
			
			CardHeader cardHeader = new CardHeader(context);
	    	cardHeader.setTitle(_booking.getCustomer().getName());
	    	cardHeader.setOtherButtonDrawable(R.drawable.btn_action_call);
	    	cardHeader.setOtherButtonVisible(true);
	    	cardHeader.setOtherButtonClickListener(new OnClickCardHeaderOtherButtonListener() 
			{
				@Override
				public void onButtonItemClick(Card card, View view) 
				{
					Log.i(TAG, "btn_action_call clicked");
					Intent callIntent = new Intent(Intent.ACTION_CALL);
					callIntent.setData(Uri.parse("tel:" + booking.getCustomer().getPhoneNumber()));
					context.startActivity(callIntent);
				}
			});
			
	    	
	    	addCardHeader(cardHeader);
	    	setOnLongClickListener(onLongClickListener);
	    	setBackgroundResourceId(R.drawable.customer_business_card_selector);
		}
		
		@Override
		public void setupInnerViewElements(ViewGroup parent, View view) 
		{
			TextView txtService = (TextView) view.findViewById(R.id.business_booking_list_booking_card_service);
			
			txtService.setText(_booking.getServiceName());
			
			TextView txtTime = (TextView) view.findViewById(R.id.business_booking_list_booking_card_date);
			txtTime.setText(Constants.DATE_TIME_FORMAT.format(_booking.getDate()));
		}
    }
}