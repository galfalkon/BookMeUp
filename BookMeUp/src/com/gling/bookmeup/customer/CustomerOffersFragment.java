package com.gling.bookmeup.customer;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gling.bookmeup.R;
import com.gling.bookmeup.main.OnClickListenerFragment;
import com.gling.bookmeup.main.ParseHelper;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

public class CustomerOffersFragment extends OnClickListenerFragment {
	private static final String TAG = "CustomerOffersFragment";
	
	private OffersListAdapter _offersAdapter;
	private Customer _customer;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    _customer = ((CustomerMainActivity)getActivity()).getCustomer();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView");
		final View view = super.onCreateView(inflater, container, savedInstanceState);
		
		_offersAdapter = new OffersListAdapter();
        ListView offersListView = (ListView) view.findViewById(R.id.customer_inbox_offersListView);
        offersListView.setAdapter(_offersAdapter);
        offersListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.i(TAG, "onItemClick");
				Toast.makeText(getActivity(), "Should open a booking wizard\nNot implemented", Toast.LENGTH_SHORT).show();
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
	
	private class OffersListAdapter extends ParseQueryAdapter<ParseHelper.Offer>
	{
		
		public OffersListAdapter() {
			super(getActivity(), new QueryFactory<ParseHelper.Offer>() {
				@Override
				public ParseQuery<ParseHelper.Offer> create() {
					ParseQuery<ParseHelper.Offer> offerQuery = new ParseQuery<ParseHelper.Offer>(ParseHelper.Offer.class).
							whereEqualTo(ParseHelper.Offer.Keys.CUSTOMER_POINTERS, _customer).
							addDescendingOrder(ParseHelper.Offer.Keys.CREATION_DATE);
					
					offerQuery.include(ParseHelper.Offer.Keys.BUSINESS_POINTER);
					
					return offerQuery;
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
				convertView = inflator.inflate(R.layout.customer_offer_list_item, null);
			}

			final TextView txtBusinessName = (TextView) convertView.findViewById(R.id.customer_offer_list_item_txtBusinessName);
			final TextView txtExpiration = (TextView) convertView.findViewById(R.id.customer_offer_list_item_txtExpiration);
			final TextView txtDisount = (TextView) convertView.findViewById(R.id.customer_offer_list_item_txtDiscount);

			txtBusinessName.setText(offer.getBusinessName());
			txtExpiration.setText(ParseHelper.Offer.EXPIRATION_DATE_FORMAT.format(offer.getExpirationData()));
			txtDisount.setText(offer.getDiscount() + "%");
			
			return convertView;
		}
	}
}