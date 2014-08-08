package com.gling.bookmeup.business.fragments;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;

import com.gling.bookmeup.R;
import com.gling.bookmeup.main.OnClickListenerFragment;
import com.gling.bookmeup.main.ParseHelper.BookingClass;
import com.gling.bookmeup.main.ParseHelper.BusinessClass;
import com.gling.bookmeup.main.ParseHelper.CustomerClass;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class BusinessBookingsFragment extends OnClickListenerFragment {

	private static final String TAG = "BusinessBookingsFragment";

	private SimpleExpandableListAdapter _expandableListAdapter;
	
	Map<String, String> _pendingBookingsGroupHeaderData;
	private List<Map<String, String>> _pendingBookingsData;
	
	Map<String, String> _approvedBookingsGroupHeaderData;
	private List<Map<String, String>> _approvedBookingsData;
	
	// TODO: Temporary! The businessId should be saved in the shared preferences during the profile creation. 
	private static final String BUSINESS_ID = "1Q5MxL3CzB";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);

		// Initialize the group header data
		List<Map<String, String>> groupHeadersData = new ArrayList<Map<String, String>>();
		
		_pendingBookingsGroupHeaderData = new HashMap<String, String>();
		_pendingBookingsGroupHeaderData.put(ExpandableListKeys.GroupHeader.HEADER_TITLE, "Pending");
		_pendingBookingsGroupHeaderData.put(ExpandableListKeys.GroupHeader.NUM_OF_ITEMS, "(0)");
		groupHeadersData.add(_pendingBookingsGroupHeaderData);
		
		_approvedBookingsGroupHeaderData = new HashMap<String, String>();
		_approvedBookingsGroupHeaderData.put(ExpandableListKeys.GroupHeader.HEADER_TITLE, "Approved");
		_approvedBookingsGroupHeaderData.put(ExpandableListKeys.GroupHeader.NUM_OF_ITEMS, "(0)");
		groupHeadersData.add(_approvedBookingsGroupHeaderData);
		
		// Initialize the group items lists
		List<List<Map<String, String>>> listOfChildGroups = new ArrayList<List<Map<String, String>>>();

		_pendingBookingsData = new ArrayList<Map<String, String>>();
		listOfChildGroups.add(_pendingBookingsData);
		
		_approvedBookingsData = new ArrayList<Map<String,String>>();
		listOfChildGroups.add(_approvedBookingsData);
		
		_expandableListAdapter = new SimpleExpandableListAdapter(
				getActivity(),
				
				groupHeadersData,
				R.layout.business_booking_list_header,
				new String[] { ExpandableListKeys.GroupHeader.HEADER_TITLE, ExpandableListKeys.GroupHeader.NUM_OF_ITEMS},
				new int[] {R.id.expandable_bookings_list_header_txtTitle, R.id.expandable_bookings_list_header_txtNumOfItems},
				
				listOfChildGroups,
				R.layout.business_booking_list_item, 
				new String[] { ExpandableListKeys.GroupItem.CLIENT_NAME, ExpandableListKeys.GroupItem.SERVICES, ExpandableListKeys.GroupItem.DATE }, 
				new int[] { R.id.expandable_bookings_list_item_txtClientName, R.id.expandable_bookings_list_item_txtServices, R.id.expandable_bookings_list_item_txtDate });

		ExpandableListView expandableListView = (ExpandableListView) view.findViewById(R.id.business_bookings_explistViewBookings);
		expandableListView.setAdapter(_expandableListAdapter);

		return view;
	}

	@Override
	public void onClick(View v) {		
		switch (v.getId()) {
		case R.id.business_bookings_btnUpdate:
			Log.i(TAG, "btnUpdate clicked");
			inflateListsWithFutureBookings(); 
			break; 
		}
	}

	@Override
	protected int getFragmentLayoutId() {
		return R.layout.business_bookings_fragment;
	}

	private void inflateListsWithFutureBookings() {
		// TODO: The businessId should be saved in the shared preferences during the profile creation. 
		final String businessId = "UwnJrO4XIq";
		final ParseQuery<ParseObject> innerBusinessPointerQuery = new ParseQuery<ParseObject>(BusinessClass.CLASS_NAME).
				whereEqualTo(BusinessClass.Keys.ID, businessId);
		
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(BookingClass.CLASS_NAME).
				whereMatchesQuery(BookingClass.Keys.BUSINESS_POINTER, innerBusinessPointerQuery).
				whereGreaterThan(BookingClass.Keys.DATE, new Date());
		query.include(BookingClass.Keys.BUSINESS_POINTER);
		query.include(BookingClass.Keys.CUSTOMER_POINTER);

		final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...");
		
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				progressDialog.dismiss();
				Log.i(TAG, "Done querying future bookings");
				if (e != null) {
					Log.e(TAG, "Exception occurred: " + e.getMessage());
					return;
				}

				_approvedBookingsData.clear();
				_pendingBookingsData.clear();
				
				for (ParseObject parseObject : objects) {
					final Booking booking = new Booking(parseObject);
					if (booking._isApproved) {
						_approvedBookingsData.add(booking.toMap());
					} else {
						_pendingBookingsData.add(booking.toMap());
					}
				}

				_pendingBookingsGroupHeaderData.put(ExpandableListKeys.GroupHeader.NUM_OF_ITEMS, "(" + String.valueOf(_pendingBookingsData.size()) + ")");
				_approvedBookingsGroupHeaderData.put(ExpandableListKeys.GroupHeader.NUM_OF_ITEMS, "(" + String.valueOf(_approvedBookingsData.size()) + ")");
				_expandableListAdapter.notifyDataSetChanged();
				
				Log.i(TAG, "#Pending bookings = " + _pendingBookingsData.size() + ", #Approved bookings = " + _approvedBookingsData.size());
			}
		});
	}

	private static class ExpandableListKeys {
		public static class GroupHeader {
			public final static String HEADER_TITLE = "TITLE";
			public final static String NUM_OF_ITEMS = "NUM_OF_ITEMS";
		}
		
		public static class GroupItem {
			public final static String CLIENT_NAME = "CLIENT_NAME";
			public final static String SERVICES = "SERVICES";
			public final static String DATE = "DATE";
		}
	}
	
	private static class Booking {
		public final String _businessName, _clientName, _serviceName;
		public final Date _date;
		public final boolean _isApproved;

		public Booking(ParseObject parseObject) {
			_businessName = parseObject.getParseObject(BookingClass.Keys.BUSINESS_POINTER).getString(BusinessClass.Keys.NAME);
			_clientName = parseObject.getParseObject(BookingClass.Keys.CUSTOMER_POINTER).getString(CustomerClass.Keys.NAME);
			_serviceName = parseObject.getString(BookingClass.Keys.SERVICES);
			_date = parseObject.getDate(BookingClass.Keys.DATE);
			_isApproved = parseObject.getBoolean(BookingClass.Keys.IS_APPROVED);
		}

		public Map<String, String> toMap() {
			Map<String, String> map = new HashMap<String, String>();
			map.put(ExpandableListKeys.GroupItem.CLIENT_NAME, _clientName);
			map.put(ExpandableListKeys.GroupItem.SERVICES, _serviceName);
			map.put(ExpandableListKeys.GroupItem.DATE, _date.toString());
			return map;
		}
	}
}