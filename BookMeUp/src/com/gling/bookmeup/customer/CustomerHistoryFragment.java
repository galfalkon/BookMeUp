package com.gling.bookmeup.customer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.gling.bookmeup.R;
import com.gling.bookmeup.main.OnClickListenerFragment;
import com.gling.bookmeup.main.ParseHelper.Booking;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

public class CustomerHistoryFragment extends OnClickListenerFragment implements OnClickListener {

	private static final String TAG = "CustomerHistoryFragment";

	private List<String> _businessesVisited;
	private ArrayAdapter<String> _businessesAdapter;
	
	@Override
	protected int getFragmentLayoutId() {
		return R.layout.customer_history_fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		
		_businessesVisited = new ArrayList<String>();
		_businessesAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, _businessesVisited);
		
		/*
		 * Build a query that represents bookings with the following properties:
		 * 		For this customer
		 * 		Before today
		 *		Were approved
		 */
		ParseQuery<Booking> query = new ParseQuery<Booking>(Booking.CLASS_NAME).
				whereEqualTo(Booking.Keys.CUSTOMER_POINTER, Customer.getCurrentCustomer()).
				whereLessThan(Booking.Keys.DATE, new Date()).
				whereEqualTo(Booking.Keys.STATUS, Booking.Status.APPROVED).
				orderByDescending(Booking.Keys.DATE);
		query.include(Booking.Keys.CUSTOMER_POINTER);
		query.include(Booking.Keys.BUSINESS_POINTER);
		query.include(Booking.Keys.SERVICE_POINTER);
		
		final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...");
		query.findInBackground(new FindCallback<Booking>() {
			@Override
			public void done(List<Booking> objects, ParseException e) {
				Log.i(TAG, "query done");
				
				progressDialog.dismiss();
				if (e != null) {
					Log.e(TAG, "Exception: " + e.getMessage());
					return;
				}
				
				for (Booking bookingParseObject : objects) {
					String businessName = bookingParseObject.getBusinessName();
					if (!_businessesVisited.contains(businessName)) {
						_businessesVisited.add(businessName);
					}
				}
				_businessesAdapter.notifyDataSetChanged();
			}
		});
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView");
		View view = super.onCreateView(inflater, container, savedInstanceState);
		
		ListView busienssesListView = (ListView) view.findViewById(R.id.customer_history_listViewBusinesses);
		busienssesListView.setAdapter(_businessesAdapter);
		
		return view;
	}
	
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		Log.i(TAG, "onCreateView");
//
//		View rootView = inflater.inflate(R.layout.fragment_main_screen,
//				container, false);
//		
//		// Set event listeners
//		rootView.findViewById(R.id.main_screen_btnBabrbers).setOnClickListener(this);
//		rootView.findViewById(R.id.main_screen_btnCosmetics).setOnClickListener(this);
//
//		return rootView;
//	}
//
	@Override
	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.customer_main_screen_btnBabrbers:
//			Log.i(TAG, "main_screen_btnBabrbers clicked");
//			break;
//		case R.id.customer_main_screen_btnCosmetics:
//			Log.i(TAG, "main_screen_btnCosmetics clicked");
//			break;
//		case R.id.customer_main_screen_btnDentists:
//			Log.i(TAG, "btnDentists clicked");
//			break;
//		case R.id.customer_main_screen_btnGarages:
//			Log.i(TAG, "btnGarages clicked");
//			break;
//		}
//		
//		Toast.makeText(getActivity(), "Not implemented", Toast.LENGTH_SHORT).show();
	}
}
