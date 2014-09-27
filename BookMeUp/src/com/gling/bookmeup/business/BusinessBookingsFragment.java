package com.gling.bookmeup.business;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.Card.OnCardClickListener;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.prototypes.CardSection;
import it.gmariotti.cardslib.library.prototypes.SectionedCardAdapter;
import it.gmariotti.cardslib.library.view.CardListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gling.bookmeup.R;
import com.gling.bookmeup.main.OnClickListenerFragment;
import com.gling.bookmeup.main.ParseHelper.Booking;
import com.gling.bookmeup.main.ParseHelper.Booking.Status;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class BusinessBookingsFragment extends OnClickListenerFragment {

    private static final String TAG = "BusinessBookingsFragment";
    
    private List<Booking> _bookings;
    private List<Card> _cards;
    private CardArrayAdapter _cardArrayAdapter; 
    private SectionedCardAdapter _sectionedCardAdapter;
    
    // TODO: Temporary! The businessId should be saved in the shared preferences during the profile creation. 
    private static final String BUSINESS_ID = "rsWO5YJW9u";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _cards = new ArrayList<Card>();
        _bookings = new ArrayList<Booking>();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        _cardArrayAdapter = new CardArrayAdapter(getActivity(), _cards);

        // Define sections
        List<CardSection> sections =  new ArrayList<CardSection>();
        sections.add(new CardSection(0, getString(R.string.business_bookings_list_pending_header)));
        sections.add(new CardSection(0, getString(R.string.business_bookings_list_approved_header)));

        // Define a SectionedAdapter
        _sectionedCardAdapter = new SectionedCardAdapter(getActivity(), _cardArrayAdapter);
        CardSection[] sectionsArray = new CardSection[sections.size()];
        _sectionedCardAdapter.setCardSections(sections.toArray(sectionsArray));

        CardListView listView = (CardListView) view.findViewById(R.id.business_bookings_cardListViewBookings);
        listView.setExternalAdapter(_sectionedCardAdapter, _cardArrayAdapter);
        
        inflateListWithFutureBookings();
        
        return view;
    }

    @Override
    public void onClick(View v) {       
        switch (v.getId()) {
        case R.id.business_bookings_btnUpdate:
            Log.i(TAG, "btnUpdate clicked");
            inflateListWithFutureBookings(); 
            break; 
        }
    }

    @Override
    protected int getFragmentLayoutId() {
        return R.layout.business_bookings_fragment;
    }

    private void inflateListWithFutureBookings() {
        // TODO: The businessId should be saved in the shared preferences during the profile creation. 
        final ParseQuery<ParseObject> innerBusinessPointerQuery = new ParseQuery<ParseObject>(Business.CLASS_NAME).
                whereEqualTo(Business.Keys.ID, BUSINESS_ID);
        
        ParseQuery<Booking> query = new ParseQuery<Booking>(Booking.CLASS_NAME).
                whereMatchesQuery(Booking.Keys.BUSINESS_POINTER, innerBusinessPointerQuery).
                whereGreaterThan(Booking.Keys.DATE, new Date()).
                orderByAscending(Booking.Keys.STATUS);
        query.include(Booking.Keys.BUSINESS_POINTER);
        query.include(Booking.Keys.CUSTOMER_POINTER);
        query.include(Booking.Keys.SERVICE_POINTER);

        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...");
        query.findInBackground(new FindCallback<Booking>() {
            @Override
            public void done(List<Booking> objects, ParseException e) {
                progressDialog.dismiss();
                Log.i(TAG, "Done querying future bookings");
                if (e != null) {
                    Log.e(TAG, "Exception occurred: " + e.getMessage());
                    return;
                }
                
                _bookings = objects;
                arrangeBookingsInCardListView();
            }
        });
    }
    
    /*
     * Precondition: bookings are sorted their status (in an ascending order)
     */
	private void arrangeBookingsInCardListView() {
		_cards.clear();
        
        int firstPendingBooking = 0, firstApprovedBooking = _bookings.size(); 
        for (int i = _bookings.size() - 1; i >= 0; i--)
        {
        	Booking booking = _bookings.get(i);
        	
        	switch (booking.getStatus())
        	{
        	case Booking.Status.PENDING:
        		firstPendingBooking = i;
        		break;
        	case Booking.Status.APPROVED:
        		firstApprovedBooking = i;
        		break;
        	case Booking.Status.CANCELED:
        		continue;
        	}
        	
        	_cards.add(0, convertBookingToCard(booking));
        }
        
        List<CardSection> newCardSections = new ArrayList<CardSection>();
        newCardSections.add(new CardSection(firstPendingBooking, getString(R.string.business_bookings_list_pending_header)));
        newCardSections.add(new CardSection(firstApprovedBooking, getString(R.string.business_bookings_list_approved_header)));
        CardSection[] cardSectionArray = new CardSection[2];
        _sectionedCardAdapter.setCardSections(newCardSections.toArray(cardSectionArray));
        
        _cardArrayAdapter.notifyDataSetChanged();
	}
    
	private Card convertBookingToCard(final Booking booking) {
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
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

				switch (booking.getStatus())
				{
				case Booking.Status.PENDING:
					Log.i(TAG, "Pending booking clicked");
		            builder.setMessage(R.string.business_bookings_list_pending_click_dialog)
		            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int id) {
		                    Log.i(TAG, "Approving pending booking");
		                    booking.setStatus(Status.APPROVED);
		                    booking.saveInBackground();
		                    
		                    _bookings.remove(booking);
		                    _bookings.add(booking);
		                    arrangeBookingsInCardListView();
		                    
		                    // TODO: Notify customer using push notification
		                }
		            })
		            .setNegativeButton(R.string.cancel, null);
					break;
				case Booking.Status.APPROVED:
		            Log.i(TAG, "Approved booking clicked");
		            builder.setMessage(R.string.business_bookings_list_approved_click_dialog)
		            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int id) {
		                    Log.i(TAG, "Deleting approved booking");
		                    booking.setStatus(Status.CANCELED);
		                    booking.saveInBackground();
		                    
		                    _bookings.remove(booking);
		                    arrangeBookingsInCardListView();
		                    // TODO: Notify customer using push notification
		                }
		            })
		            .setNegativeButton(R.string.cancel, null);
					break;
				}
				
		        builder.show();
			}
    	});
    	
    	return card;
    }
}