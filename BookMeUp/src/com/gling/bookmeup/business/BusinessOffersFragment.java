package com.gling.bookmeup.business;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardExpand;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardListView;

import java.util.List;

import android.app.ProgressDialog;
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
import com.gling.bookmeup.main.ParseHelper.Offer;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

public class BusinessOffersFragment extends OnClickListenerFragment {
	private static final String TAG = "BusinessOffersFragment";
	
	private GenericCardArrayAdapter<Offer> _offersAdapter;
	private IObservableList<Offer> _offers;
	
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
		
        _offersAdapter = GenericCardArrayAdapter.<Offer>create(getActivity(), _offers, new OfferCardsGenerator());
        CardListView offersListView = (CardListView) view.findViewById(R.id.business_offers_listViewOffersNew);
        offersListView.setAdapter(_offersAdapter);
        
        ParseQuery<Offer> parseQuery = new ParseQuery<Offer>(Offer.class)
	    		.whereEqualTo(ParseHelper.Offer.Keys.BUSINESS_POINTER, Business.getCurrentBusiness());
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...");
	    parseQuery.findInBackground(new FindCallback<ParseHelper.Offer>() {
			
			@Override
			public void done(List<Offer> objects, ParseException e) {
				Log.i(TAG, "findInBackground done");
				progressDialog.dismiss();
				if (e != null)
				{
					Log.e(TAG, "Exception: " + e.getMessage());
					return;
				}
				
				_offers.addAll(objects);
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
		public Card generateCard(Offer offer) {
			CardHeader cardHeader= new CardHeader(getActivity());
			cardHeader.setButtonExpandVisible(true);
			
			CardExpand cardExpand = new CardExpand(getActivity());
			cardExpand.setTitle("Valid until " + Offer.EXPIRATION_DATE_FORMAT.format(offer.getExpirationData()));
			
			Card card = new Card(getActivity());
			card.addCardHeader(cardHeader);
			card.setTitle(offer.getDiscount() + "% off @ " + offer.getBusinessName());
			card.addCardExpand(cardExpand);
			
			return card;
		}
	}
}