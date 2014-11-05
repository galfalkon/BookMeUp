package com.gling.bookmeup.business;

import it.gmariotti.cardslib.library.internal.Card;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gling.bookmeup.main.Constants;
import com.gling.bookmeup.main.GenericCardArrayAdapter;
import com.gling.bookmeup.main.ICardGenerator;
import com.gling.bookmeup.main.IObservableList;
import com.gling.bookmeup.main.ObservableArrayList;
import com.gling.bookmeup.main.OnClickListenerFragment;
import com.gling.bookmeup.main.views.BaseListViewWrapperView.DisplayMode;
import com.gling.bookmeup.main.views.CardListViewWrapperView;
import com.gling.bookmeup.sharedlib.parse.Business;
import com.gling.bookmeup.sharedlib.parse.ParseHelper;
import com.gling.bookmeup.sharedlib.parse.ParseHelper.Offer;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

public class BusinessOffersFragment extends OnClickListenerFragment {
	private static final String TAG = "BusinessOffersFragment";
	
	private GenericCardArrayAdapter<Offer> _offersAdapter;
	private IObservableList<Offer> _offers;
	
	private CardListViewWrapperView _offersListView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    _offers = new ObservableArrayList<Offer>();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView");
		final View view = super.onCreateView(inflater, container, savedInstanceState);
		
        _offersAdapter = new GenericCardArrayAdapter<Offer>(getActivity(), _offers, new OfferCardsGenerator());
        _offersListView = (CardListViewWrapperView) view.findViewById(R.id.business_offers_listViewOffersNew);
        _offersListView.setAdapter(_offersAdapter);
        
        ParseQuery<Offer> parseQuery = new ParseQuery<Offer>(Offer.class)
	    		.whereEqualTo(ParseHelper.Offer.Keys.BUSINESS_POINTER, Business.getCurrentBusiness());
        _offersListView.setDisplayMode(DisplayMode.LOADING_VIEW);
	    parseQuery.findInBackground(new FindCallback<ParseHelper.Offer>() {
			
			@Override
			public void done(List<Offer> objects, ParseException e) {
				Log.i(TAG, "findInBackground done");
				if (e != null)
				{
					Log.e(TAG, "Exception: " + e.getMessage());
					return;
				}
				_offers.addAll(objects);
				
				DisplayMode newDisplayMode = _offers.isEmpty()? DisplayMode.NO_ITEMS_VIEW : DisplayMode.LIST_VIEW;  
				_offersListView.setDisplayMode(newDisplayMode);
			}
		});
		
		return view;
	}
	
	@Override
	public void onClick(View view) {
	}

	@Override
	protected int getFragmentLayoutId() {
		return R.layout.business_offers_fragment;
	}
	
	private class OfferCardsGenerator implements ICardGenerator<Offer>
	{
		@Override
		public Card generateCard(Offer offer) 
		{
			return new OfferCard(getActivity(), offer);
		}
	}
	
	private static class OfferCard extends Card
    {
    	private final Offer _offer;
    	private final Context _context;
    	
		public OfferCard(Context context, Offer offer) 
		{
			super(context, R.layout.business_offer_list_offer_card);
			
			_context = context;
			_offer = offer;
			setBackgroundResourceId(R.drawable.customer_business_card_selector);
		}
		
		@Override
		public void setupInnerViewElements(ViewGroup parent, View view) 
		{
			TextView txtDiscount = (TextView) view.findViewById(R.id.business_offer_list_offer_card_discount);
			txtDiscount.setText(_offer.getDiscount() + "%");
			
			TextView txtExpirationDate = (TextView) view.findViewById(R.id.business_offer_list_offer_card_expiration_date);
			txtExpirationDate.setText(Constants.DATE_FORMAT.format(_offer.getExpirationData()));
			
			TextView txtStatus = (TextView) view.findViewById(R.id.business_offer_list_offer_card_status);
			boolean isActive = _offer.getExpirationData().after(new Date());
			String statusDescription = isActive? "Active" : "Not Active";
			int statusColorResource = isActive? android.R.color.holo_green_light : android.R.color.holo_red_light;    
			txtStatus.setText(statusDescription);
			txtStatus.setTextColor(_context.getApplicationContext().getResources().getColor(statusColorResource));
		}
    }
}