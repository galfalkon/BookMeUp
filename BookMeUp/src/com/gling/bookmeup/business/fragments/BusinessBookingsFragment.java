package com.gling.bookmeup.business.fragments;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.gling.bookmeup.R;
import com.gling.bookmeup.main.OnClickListenerFragment;
import com.gling.bookmeup.main.ParseHelper.Booking;
import com.gling.bookmeup.main.ParseHelper.BusinessClass;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class BusinessBookingsFragment extends OnClickListenerFragment implements OnChildClickListener {

	private static final String TAG = "BusinessBookingsFragment";

	private BaseExpandableListAdapter _expandableListAdapter;
	
	private List<List<Booking>> _bookings;
	
	private static enum ExpandableListGroupIds {
		PENDING_GROUP_ID,
		APPROVED_GROUP_ID
	}
	
	// TODO: Temporary! The businessId should be saved in the shared preferences during the profile creation. 
	private static final String BUSINESS_ID = "mUhs7IdMT7";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_bookings = new ArrayList<List<Booking>>();
		
		for (int i = 0; i < ExpandableListGroupIds.values().length; i++) {
			_bookings.add(new ArrayList<Booking>());
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);

		_expandableListAdapter = new BookingsExpandableListAdapter();
		
		ExpandableListView expandableListView = (ExpandableListView) view.findViewById(R.id.business_bookings_explistViewBookings);
		expandableListView.setAdapter(_expandableListAdapter);
		
		expandableListView.setOnChildClickListener(this);

		inflateListWithFutureBookings();
		
		return view;
	}

	@Override
	public void onClick(View v) {		
		switch (v.getId()) {
		case R.id.business_bookings_btnUpdate:
			Log.i(TAG, "btnUpdate clicked");
			inflateListWithFutureBookings(); 
			break; 
		}
	}

	@Override
	protected int getFragmentLayoutId() {
		return R.layout.business_bookings_fragment;
	}
	
	@Override
	public boolean onChildClick(final ExpandableListView parent, View v, final int groupPosition, final int childPosition, final long id) {
		Log.i(TAG, "onChildClick!");
		
		// Build dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		switch (ExpandableListGroupIds.values()[groupPosition]) {
		case PENDING_GROUP_ID:
			Log.i(TAG, "Pending booking clicked");
			builder.setMessage(R.string.business_bookings_list_pending_click_dialog)
	        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int id) {
	            	Log.i(TAG, "Approving pending booking");
	            	Toast.makeText(getActivity(), "Not implemented", Toast.LENGTH_SHORT).show();
	            }
	        })
	        .setNegativeButton("Cancel", null);
			break;
		case APPROVED_GROUP_ID:
			Log.i(TAG, "Approved booking clicked");
			builder.setMessage(R.string.business_bookings_list_approved_click_dialog)
	        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int id) {
	            	Log.i(TAG, "Canceling approved booking");
	            	Toast.makeText(getActivity(), "Not implemented", Toast.LENGTH_SHORT).show();
	            }
	        })
	        .setNegativeButton("Cancel", null);
			break;
		}
        builder.show();
        
		return false;
	}

	private void inflateListWithFutureBookings() {
		// TODO: The businessId should be saved in the shared preferences during the profile creation. 
		final ParseQuery<ParseObject> innerBusinessPointerQuery = new ParseQuery<ParseObject>(BusinessClass.CLASS_NAME).
				whereEqualTo(BusinessClass.Keys.ID, BUSINESS_ID);
		
		ParseQuery<Booking> query = new ParseQuery<Booking>(Booking.CLASS_NAME).
				whereMatchesQuery(Booking.Keys.BUSINESS_POINTER, innerBusinessPointerQuery).
				whereGreaterThan(Booking.Keys.DATE, new Date());
		query.include(Booking.Keys.BUSINESS_POINTER);
		query.include(Booking.Keys.CUSTOMER_POINTER);

		final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...");
		
		query.findInBackground(new FindCallback<Booking>() {
			@Override
			public void done(List<Booking> objects, ParseException e) {
				progressDialog.dismiss();
				Log.i(TAG, "Done querying future bookings");
				if (e != null) {
					Log.e(TAG, "Exception occurred: " + e.getMessage());
					return;
				}

				for (int i = 0; i < _bookings.size(); i++) {
					_bookings.get(i).clear();
				}
				for (Booking parseObject : objects) {
					if (parseObject.getIsApproved()) {
						_bookings.get(ExpandableListGroupIds.APPROVED_GROUP_ID.ordinal()).add(parseObject);
					} else {
						_bookings.get(ExpandableListGroupIds.PENDING_GROUP_ID.ordinal()).add(parseObject);
					}
				}

				_expandableListAdapter.notifyDataSetChanged();
				
				Log.i(TAG, "#Pending bookings = " + _bookings.get(0).size() + ", #Approved bookings = " + _bookings.get(1).size());
			}
		});
	}

	private class BookingsExpandableListAdapter extends BaseExpandableListAdapter {
		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return _bookings.get(groupPosition).get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return _bookings.get(groupPosition).get(childPosition).getObjectId().hashCode();
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			LayoutInflater inflator = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (convertView == null) {
				convertView = inflator.inflate(R.layout.business_booking_list_item, null);
			}
			
			TextView clientNameTxtView = (TextView)convertView.findViewById(R.id.expandable_bookings_list_item_txtClientName);
			TextView servicesTxtView = (TextView)convertView.findViewById(R.id.expandable_bookings_list_item_txtServices);
			TextView dateTxtView = (TextView)convertView.findViewById(R.id.expandable_bookings_list_item_txtDate);
			
			clientNameTxtView.setText(_bookings.get(groupPosition).get(childPosition).getClientName());
			servicesTxtView.setText(_bookings.get(groupPosition).get(childPosition).getServiceName());
			dateTxtView.setText(_bookings.get(groupPosition).get(childPosition).getDate().toString());
			
			return convertView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return _bookings.get(groupPosition).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return _bookings.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			return _bookings.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			LayoutInflater inflator = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (convertView == null) {
				convertView = inflator.inflate(R.layout.business_booking_list_header, null);
			}
			
			TextView titleTextView = (TextView)convertView.findViewById(R.id.expandable_bookings_list_header_txtTitle);
			TextView numOfItemsTextView = (TextView)convertView.findViewById(R.id.expandable_bookings_list_header_txtNumOfItems);
			
			switch (groupPosition) {
			case 0:
				titleTextView.setText("Pending");
				break;
			case 1:
				titleTextView.setText("Approved");
			}
			numOfItemsTextView.setText("(" + _bookings.get(groupPosition).size() + ")");
			
			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}
}