package com.gling.bookmeup.fragments;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.gling.bookmeup.R;
import com.gling.bookmeup.main.OnClickListenerFragment;
import static com.gling.bookmeup.main.ParseHelper.BookingsClass;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class BusinessBookingsFragment extends OnClickListenerFragment {
	
	private static final String TAG = "BusinessBookingsFragment";
	
	private ArrayAdapter<String> _pendingBookingsAdapter;
	private List<String> _pendingBookings;
	
	private ArrayAdapter<String> _approvedBookingsAdapter;
	private List<String> _approvedBookings;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		
		_pendingBookings = new ArrayList<String>();
		_pendingBookingsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, _pendingBookings);
		
		_approvedBookings = new ArrayList<String>();
		_approvedBookingsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, _approvedBookings);
		
		ListView pendingBookingsListView = (ListView)view.findViewById(R.id.business_bookings_listViewPendingBookings);
		pendingBookingsListView.setAdapter(_pendingBookingsAdapter);
		
		ListView approvedBookingsListView = (ListView)view.findViewById(R.id.business_bookings_listViewApprovedBookings);
		approvedBookingsListView.setAdapter(_approvedBookingsAdapter);
		
		return view;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.business_bookings_btnUpdate:
			Log.i(TAG, "btnUpdate clicked");
			inflateListsWithFutureBookings("TEST");
			break;
		}
	}

	@Override
	protected int getFragmentLayoutId() {
		return R.layout.fragment_business_bookings;
	}
	
	private void inflateListsWithFutureBookings(final String businessId) {
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(BookingsClass.CLASS_NAME).
				whereEqualTo(BookingsClass.Keys.BUSINESS_ID, businessId).
				whereGreaterThan(BookingsClass.Keys.DATE, new Date());

		final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...");
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				progressDialog.dismiss();
				Log.i(TAG, "Done queriny future bookings");
				if (e != null) {
					Log.e(TAG, "Exception occurred: " + e.getMessage());
					return;
				}
				
				_approvedBookings.clear();
				_pendingBookings.clear();
				
				for (ParseObject object : objects) {
					if (object.getBoolean(BookingsClass.Keys.IS_APPROVED)) {
						_approvedBookings.add(object.getDate(BookingsClass.Keys.DATE).toString());
					} else {
						_pendingBookings.add(object.getDate(BookingsClass.Keys.DATE).toString());
					}
				}
				
				Log.i(TAG, "#Pending bookings = " + _pendingBookings.size() + ", #Approved bookings = " + _approvedBookings.size());
				_approvedBookingsAdapter.notifyDataSetChanged();
				_pendingBookingsAdapter.notifyDataSetChanged();
			}
		});
	}
}