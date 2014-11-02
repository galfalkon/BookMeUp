package com.gling.bookmeup.customer;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.Card.OnCardClickListener;
import it.gmariotti.cardslib.library.internal.CardExpand;
import it.gmariotti.cardslib.library.internal.CardHeader;

import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gling.bookmeup.main.Constants;
import com.gling.bookmeup.main.GenericCardArrayAdapter;
import com.gling.bookmeup.main.ICardGenerator;
import com.gling.bookmeup.main.IObservableList;
import com.gling.bookmeup.main.ObservableArrayList;
import com.gling.bookmeup.main.OnClickListenerFragment;
import com.gling.bookmeup.main.views.BaseListViewWrapperView.DisplayMode;
import com.gling.bookmeup.main.views.CardListViewWrapperView;
import com.gling.bookmeup.sharedlib.parse.Business;
import com.gling.bookmeup.sharedlib.parse.Customer;
import com.gling.bookmeup.sharedlib.parse.ParseHelper;
import com.gling.bookmeup.sharedlib.parse.ParseHelper.Offer;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class CustomerOffersFragment extends OnClickListenerFragment {
	private static final String TAG = "CustomerOffersFragment";
	
	final private Fragment _thisFragment = this;
	
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
        _offersListView = (CardListViewWrapperView) view.findViewById(R.id.customer_inbox_offersListView);
        _offersListView.setAdapter(_offersAdapter);
        
		ParseQuery<ParseHelper.Offer> parseQuery = new ParseQuery<ParseHelper.Offer>(ParseHelper.Offer.class).
				whereEqualTo(ParseHelper.Offer.Keys.CUSTOMER_POINTERS, Customer.getCurrentCustomer()).
				addDescendingOrder(ParseHelper.Offer.Keys.CREATION_DATE);
		
		parseQuery.include(ParseHelper.Offer.Keys.BUSINESS_POINTER);
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
		return R.layout.customer_offer_list_fragment;
	}
	
	private class OfferCardsGenerator implements ICardGenerator<Offer>
	{
		@Override
		public Card generateCard(Offer offer) {
			CardHeader cardHeader= new CardHeader(getActivity());
			cardHeader.setButtonExpandVisible(true);
			
			CardExpand cardExpand = new CardExpand(getActivity());
			cardExpand.setTitle("Valid until " + Constants.DATE_TIME_FORMAT.format(offer.getExpirationData()));
			//TODO: move from expand to the card itself
			
			Card card = new Card(getActivity());
			card.setId(offer.getObjectId());
			card.addCardHeader(cardHeader);
			card.setTitle(offer.getDiscount() + "% off @ " + offer.getBusinessName());
			card.addCardExpand(cardExpand);
			
			card.setOnClickListener(new OnCardClickListener() {
				
				@Override
				public void onClick(Card offerCard, View arg1) {					
					Log.i(TAG, "offer was clicked");
					String offerId = offerCard.getId();
					for (Offer offer : _offers) {
						if (offer.getObjectId().equals(offerId)) {
							Business business = offer.getBusiness();
							Activity activity = getActivity();
							if (activity instanceof CustomerMainActivity) {
								CustomerMainActivity customerActivity = (CustomerMainActivity)activity;
								customerActivity.setChosenBusiness(business);
								customerActivity.setLastFragment(_thisFragment);
							}
							
							Fragment fragment = new CustomerBookingProfileFragment();
							Bundle bundle = new Bundle();
							bundle.putSerializable(CustomerBookingProfileFragment.OFFER_EXPIRATION_DATE, offer.getExpirationData());
							bundle.putInt(CustomerBookingProfileFragment.OFFER_DISCOUNT, offer.getDiscount());
							fragment.setArguments(bundle);
							getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
							return;
						}
					}
					Log.i(TAG, "Business Dialog - could not find offer");
					Crouton.showText(getActivity(), "Could not find offer", Style.ALERT);
					return;
				}
			});
			
			return card;
		}
	}
}