package com.gling.bookmeup.business;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.Card.OnCardClickListener;
import it.gmariotti.cardslib.library.internal.CardHeader;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ViewSwitcher;

import com.gling.bookmeup.R;
import com.gling.bookmeup.main.GenericCardArrayAdapter;
import com.gling.bookmeup.main.ICardGenerator;
import com.gling.bookmeup.main.IObservableList;
import com.gling.bookmeup.main.ObservableArrayList;
import com.gling.bookmeup.main.OnClickListenerFragment;
import com.gling.bookmeup.main.ParseHelper.Booking;
import com.gling.bookmeup.main.ParseHelper.Booking.Status;
import com.gling.bookmeup.main.views.BaseListViewWrapperView.DisplayMode;
import com.gling.bookmeup.main.views.CardListViewWrapperView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

public class BusinessBookingsFragment extends OnClickListenerFragment {

    private static final String TAG = "BusinessBookingsFragment";
    
    private IObservableList<Booking> _pendingBookings, _approvedBookings;
    private GenericCardArrayAdapter<Booking> _pendingBookingsAdapter, _approvedBookingsAdapter;

	private Button _btnPending, _btnApproved;
	private ViewSwitcher _viewSwitcher;
	
	private CardListViewWrapperView _pendingBookingsListView, _approvedBookingsListView;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        _pendingBookings = new ObservableArrayList<Booking>();
        _approvedBookings = new ObservableArrayList<Booking>();
        _pendingBookingsAdapter = new GenericCardArrayAdapter<Booking>(getActivity(), _pendingBookings, new PendingBookingCardGenerator());
        _approvedBookingsAdapter = new GenericCardArrayAdapter<Booking>(getActivity(), _approvedBookings, new ApprovedBookingCardGenerator());
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        
        _btnPending = (Button)view.findViewById(R.id.business_bookings_btnPending);
        _btnApproved = (Button)view.findViewById(R.id.business_bookings_btnApproved);
        
        _pendingBookingsListView = (CardListViewWrapperView)view.findViewById(R.id.business_bookings_cardListViewPendingBookings);
        _pendingBookingsListView.setAdapter(_pendingBookingsAdapter);
        
        _approvedBookingsListView = (CardListViewWrapperView)view.findViewById(R.id.business_bookings_cardListViewApprovedBookings);
        _approvedBookingsListView.setAdapter(_approvedBookingsAdapter);
        
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
    	_btnPending.setText(String.format("%s (%d)",getString(R.string.business_bookings_list_pending_header), _pendingBookings.size()));

    	// Update display mode
    	DisplayMode newDisplayMode = _pendingBookings.isEmpty()? DisplayMode.NO_ITEMS_VIEW : DisplayMode.LIST_VIEW;
    	_pendingBookingsListView.setDisplayMode(newDisplayMode);
    }
    
    private void updateApprovedBookingsTitleAndDisplayMode()
    {
    	// Update title
    	_btnApproved.setText(String.format("%s (%d)",getString(R.string.business_bookings_list_approved_header), _approvedBookings.size()));
    	
    	// Update display mode
    	DisplayMode newDisplayMode = _approvedBookings.isEmpty()? DisplayMode.NO_ITEMS_VIEW : DisplayMode.LIST_VIEW;
    	_approvedBookingsListView.setDisplayMode(newDisplayMode);
    }
    
    private class PendingBookingCardGenerator implements ICardGenerator<Booking>
    {
		@Override
		public Card generateCard(final Booking booking) 
		{
			CardHeader cardHeader = new CardHeader(getActivity());
	    	cardHeader.setTitle(booking.getCustomerName());
	    	
	    	Card card = new Card(getActivity());
	    	card.addCardHeader(cardHeader);
	    	card.setTitle(
	    			"Service: " + booking.getServiceName() + "\n" +
	    			"Date: " + (new SimpleDateFormat("dd-MM-yy")).format(booking.getDate()));

	    	card.setClickable(true);
	    	card.setOnClickListener(new OnCardClickListener() 
	    	{
				@Override
				public void onClick(Card card, View view) 
				{
					Log.i(TAG, "Pending booking clicked");
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		            builder.setMessage(R.string.business_bookings_list_pending_click_dialog)
		            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int id) {
		                    Log.i(TAG, "Approving pending booking");
		                    booking.setStatus(Status.APPROVED);
		                    booking.saveInBackground();
		                    
		                    _pendingBookings.remove(booking);
		                    _approvedBookings.add(booking);
		                    updatePendingBookingsTitleAndDisplayMode();
		                    updateApprovedBookingsTitleAndDisplayMode();
		                }
		            })
		            .setNegativeButton(R.string.cancel, null);
			        builder.show();
				}
	    	});
	    	
	    	return card;
		}
    }
    
    private class ApprovedBookingCardGenerator implements ICardGenerator<Booking>
    {
		@Override
		public Card generateCard(final Booking booking) 
		{
			CardHeader cardHeader = new CardHeader(getActivity());
	    	cardHeader.setTitle(booking.getCustomerName());
	    	
	    	Card card = new Card(getActivity());
	    	card.addCardHeader(cardHeader);
	    	card.setTitle(
	    			"Service: " + booking.getServiceName() + "\n" +
	    			"Date: " + (new SimpleDateFormat("dd-MM-yy")).format(booking.getDate()));

	    	card.setClickable(true);
	    	card.setOnClickListener(new OnCardClickListener() {
				
				@Override
				public void onClick(Card card, View view) {
					Log.i(TAG, "Approved booking clicked");
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		            builder.setMessage(R.string.business_bookings_list_approved_click_dialog)
		            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int id) {
		                    Log.i(TAG, "Deleting approved booking");
		                    booking.setStatus(Status.CANCELED);
		                    booking.saveInBackground();
		                    
		                    _approvedBookings.remove(booking);
		                    updateApprovedBookingsTitleAndDisplayMode();
		                }
		            })
		            .setNegativeButton(R.string.cancel, null);
		            builder.show();
				}
			});
	    	
	    	return card;
		}
    }
}