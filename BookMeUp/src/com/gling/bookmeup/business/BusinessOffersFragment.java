package com.gling.bookmeup.business;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.gling.bookmeup.R;
import com.gling.bookmeup.business.Business.Offer;
import com.gling.bookmeup.main.OnClickListenerFragment;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

public class BusinessOffersFragment extends OnClickListenerFragment {
	private static final String TAG = "BusinessOffersFragment";
	
	// TODO: Temporary! The businessId should be saved in the shared preferences during the profile creation. 
	private static final String BUSINESS_ID = "mUhs7IdMT7";

	private List<Offer> _offers;
	private ListAdapter _offersAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView");
		final View view = super.onCreateView(inflater, container, savedInstanceState);
		
		final ParseQuery<Business> query = new ParseQuery<Business>(Business.CLASS_NAME).
				whereEqualTo(Business.Keys.ID, BUSINESS_ID);
		
		final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...");
		query.findInBackground(new FindCallback<Business>() {

			@Override
			public void done(List<Business> objects, ParseException e) {
				Log.i(TAG, "findInBackground done");
				progressDialog.dismiss();
				if (e != null) {
					Log.e(TAG, "Exception: " + e.getMessage());
					return;
				}
				
				Business currentBusiness = objects.get(0);
				_offers = currentBusiness.getActiveOffers();
				_offersAdapter = new OffersListAdapter();
				ListView offersListView = (ListView) view.findViewById(R.id.business_offers_listViewOffers);
				offersListView.setAdapter(_offersAdapter);
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
	
	private class OffersListAdapter extends ArrayAdapter<Offer>
	{
		public OffersListAdapter() {
			super(getActivity(), R.id.business_offers_listViewOffers, _offers);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflator = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (convertView == null) {
				convertView = inflator.inflate(R.layout.business_offer_list_item, null);
			}
			
			final TextView txtExpiration = (TextView) convertView.findViewById(R.id.business_offer_list_item_txtExpiration);
			final TextView txtDisount = (TextView) convertView.findViewById(R.id.business_offer_list_item_txtDiscount);
			
			Offer offer = _offers.get(position);
			
			txtExpiration.setText(offer.getFormattedExpirationDate());
			txtDisount.setText(offer.getDiscount() + "%");
			
			return convertView;
		}
	}
}
