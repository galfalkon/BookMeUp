package com.gling.bookmeup.customer;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.Card.OnCardClickListener;
import it.gmariotti.cardslib.library.internal.CardThumbnail;

import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gling.bookmeup.customer.cards.CardThumbnailRoundCorners;
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
        _offersAdapter.setRowLayoutId(R.layout.list_card_thumbnail_layout);
        
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
		public Card generateCard(Offer offer) 
		{
			return new OfferCard(getActivity(), offer, new OnCardClickListener() 
			{
				
				@Override
				public void onClick(Card offerCard, View arg1) {					
					Log.i(TAG, "offer was clicked");
					String offerId = offerCard.getId();
					for (Offer offer : _offers) {
						if (offer.getObjectId().equals(offerId)) 
						{
							Business business = offer.getBusiness();
							Activity activity = getActivity();
							if (activity instanceof CustomerMainActivity) 
							{
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
		}
	}
	
	private static class OfferCard extends Card
    {
    	private final Offer _offer;
    	
		public OfferCard(Context context, Offer offer, OnCardClickListener onClickListener) 
		{
			super(context, R.layout.customer_offer_list_offer_card);
			
			_offer = offer;
			
			setOnClickListener(onClickListener);
			
			if (_offer.getBusiness().getImageFile() != null)
			{
				CardThumbnail cardThumbnail = new CardThumbnailRoundCorners(context, _offer.getBusiness().getImageFile().getUrl());
				addCardThumbnail(cardThumbnail);
			}
			
			setId(_offer.getObjectId());
		}
		
		@Override
		public void setupInnerViewElements(ViewGroup parent, View view) 
		{
			TextView txtBusinessName = (TextView) view.findViewById(R.id.customer_offer_list_offer_card_business_name);
			txtBusinessName.setText(_offer.getBusiness().getName());
			
			TextView txtDiscount = (TextView) view.findViewById(R.id.customer_offer_list_offer_card_discount);
			txtDiscount.setText(_offer.getDiscount() + "%");
			
			TextView txtExpirationDate = (TextView) view.findViewById(R.id.customer_offer_list_offer_card_expiration_date);
			txtExpirationDate.setText(Constants.DATE_FORMAT.format(_offer.getExpirationData()));
		}
    }
}