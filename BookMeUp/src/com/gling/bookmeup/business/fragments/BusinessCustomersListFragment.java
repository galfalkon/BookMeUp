package com.gling.bookmeup.business.fragments;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gling.bookmeup.R;
import com.gling.bookmeup.main.OnClickListenerFragment;
import com.gling.bookmeup.main.ParseHelper;
import com.gling.bookmeup.main.ParseHelper.BookingClass;
import com.gling.bookmeup.main.ParseHelper.CusetomerClass;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class BusinessCustomersListFragment  extends OnClickListenerFragment {
	private static final String TAG = "BusinessCustomersListFragment";
	
	private List<Customer> _filteredCustomers;
	private ArrayAdapter<Customer> _listViewAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		
		_filteredCustomers = new ArrayList<Customer>();
		_listViewAdapter = new CustomersArrayAdapter();
		
		ListView listView = (ListView)view.findViewById(R.id.business_client_list_listViewClients);
		listView.setAdapter(_listViewAdapter);
		
		return view;
	}
	
	@Override
	protected int getFragmentLayoutId() {
		return R.layout.business_customer_list_fragment;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId())
		{
		case R.id.business_client_list_btnFilterBySpendings:
			Log.i(TAG, "btnFilterBySpending clicked");
			handleSpendingsFilter();
			break;
		case R.id.business_client_list_btnFilterByLastVisit:
			Log.i(TAG, "btnFilterByLastVisit clicked");
			handleLastVisitFilter();
			break;
		}
	}
	
	private void handleLastVisitFilter() {
		Log.i(TAG, "handleSpendingFilter");
		
		//TODO: The businessId should be saved in the shared preferences during the profile creation. 
		final String businessId = "UwnJrO4XIq";
		final ParseQuery<ParseObject> innerBusinessPointerQuery = new ParseQuery<ParseObject>(Business.CLASS_NAME).
				whereEqualTo(Business.Keys.ID, businessId);
		
		Calendar today = Calendar.getInstance();
		// TODO: We should implement our own DatePickerDialog because this implementation is always localized (According to the localization device's preferences)
		DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				Log.i(TAG, "Date selected");
				
				// Get a Date instance that represents the date that was selected by the user
				GregorianCalendar selectedDateCalendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
				Date selectedDate = selectedDateCalendar.getTime();
				
				/*
				 * Build a query that represents bookings with the following properties:
				 * 		For this business
				 * 		Later than the selected date
				 * 		Before today
				 *		Were approved
				 */
				ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseHelper.BookingClass.CLASS_NAME).
						whereMatchesQuery(BookingClass.Keys.BUSINESS_POINTER, innerBusinessPointerQuery).
						whereGreaterThanOrEqualTo(BookingClass.Keys.DATE, selectedDate).
						whereLessThan(BookingClass.Keys.DATE, new Date()).
						whereEqualTo(BookingClass.Keys.IS_APPROVED, true);
				query.include(BookingClass.Keys.CUSTOMER_POINTER);
				
				executeBookingsQuery(query);
			}
		}, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DATE));
		datePickerDialog.show();
	}
	
	private void handleSpendingsFilter() {
		Log.i(TAG, "handleSpendingsFilter");

		Toast.makeText(getActivity(), "Not implemented", Toast.LENGTH_SHORT).show();
	}
	
	private void executeBookingsQuery(ParseQuery<ParseObject> query) {
		final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...");
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				progressDialog.dismiss();
				if (e != null) {
					Log.e(TAG, "Exception: " + e.getMessage());
					return;
				}
				
				Log.i(TAG, "Query is done! #objects = " + objects.size());
				_filteredCustomers.clear();
				for (ParseObject parseObject : objects) {
					Customer customer = new Customer(parseObject);
					if (!_filteredCustomers.contains(customer)) {
						_filteredCustomers.add(new Customer(parseObject));
					}
				}
				
				_listViewAdapter.notifyDataSetChanged();
			}
		});
	}
	
	private static class Customer {
		public final String _id, _customerName;

		public Customer(ParseObject bookingParseObject) {
			ParseObject customersParseObject = bookingParseObject.getParseObject(BookingClass.Keys.CUSTOMER_POINTER);
			_id = customersParseObject.getObjectId();
			_customerName = customersParseObject.getString(CusetomerClass.Keys.NAME);
		}

		@Override
		public String toString() {
			return _customerName;
		}
		
		@Override
		public boolean equals(Object other) {
			return !(other instanceof Customer) || (_id.equals(((Customer)other)._id)); 
		}
	}
	
	private class CustomersArrayAdapter extends ArrayAdapter<Customer> {
		public CustomersArrayAdapter() {
			super(getActivity(), R.layout.business_customer_list_item, _filteredCustomers);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflator = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (convertView == null) {
				convertView = inflator.inflate(R.layout.business_customer_list_item, null);
			}
			
			Customer customer = _filteredCustomers.get(position);
			
			TextView customerNameTextView = (TextView) convertView.findViewById(R.id.client_list_item_txtClientName);
			
			customerNameTextView.setText(customer._customerName);
			
			return convertView;
		}
	}
}
