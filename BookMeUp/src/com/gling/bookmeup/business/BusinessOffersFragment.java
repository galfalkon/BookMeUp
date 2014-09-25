package com.gling.bookmeup.business;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.gling.bookmeup.R;
import com.gling.bookmeup.main.OnClickListenerFragment;
import com.gling.bookmeup.main.ParseHelper;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

public class BusinessOffersFragment extends OnClickListenerFragment {
	private static final String TAG = "BusinessOffersFragment";
	
	private OffersListAdapter _offersAdapter;
	private Business _business;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    _business = ((BusinessMainActivity)getActivity()).getBusiness();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView");
		final View view = super.onCreateView(inflater, container, savedInstanceState);
		
        _offersAdapter = new OffersListAdapter();
        ListView offersListView = (ListView) view.findViewById(R.id.business_offers_listViewOffers);
        offersListView.setAdapter(_offersAdapter);
		
		return view;
	}
	
	@Override
	public void onClick(View view) {
	}

	@Override
	protected int getFragmentLayoutId() {
		return R.layout.business_offers_fragment;
	}
	
	private class OffersListAdapter extends ParseQueryAdapter<ParseHelper.Offer>
	{
		
		public OffersListAdapter() {
			super(getActivity(), new QueryFactory<ParseHelper.Offer>() {
				@Override
				public ParseQuery<ParseHelper.Offer> create() {
					return new ParseQuery<ParseHelper.Offer>(ParseHelper.Offer.class).
							whereEqualTo(ParseHelper.Offer.Keys.BUSINESS_POINTER, _business);
				}
			});
			
			addOnQueryLoadListener(new OnQueryLoadListener<ParseHelper.Offer>() {

	        	private ProgressDialog _progressDialog;
	        	
				@Override
				public void onLoaded(List<com.gling.bookmeup.main.ParseHelper.Offer> objects, Exception e) {
					_progressDialog.dismiss();
				}

				@Override
				public void onLoading() {
					_progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...");
				}
			});
		}
		
		@Override
		public View getItemView(ParseHelper.Offer offer, View convertView, ViewGroup parent) {
			LayoutInflater inflator = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (convertView == null) {
				convertView = inflator.inflate(R.layout.business_offer_list_item, null);
			}
			
			final TextView txtExpiration = (TextView) convertView.findViewById(R.id.business_offer_list_item_txtExpiration);
			final TextView txtDisount = (TextView) convertView.findViewById(R.id.business_offer_list_item_txtDiscount);

			txtExpiration.setText(ParseHelper.Offer.EXPIRATION_DATE_FORMAT.format(offer.getExpirationData()));
			txtDisount.setText(offer.getDiscount() + "%");
			
			return convertView;
		}
	}
}
