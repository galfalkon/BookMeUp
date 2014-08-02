package com.gling.bookmeup.fragments;

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
import com.gling.bookmeup.main.ParseHelper.BookingsClass;
import com.gling.bookmeup.main.ParseHelper.BusinessClass;
import com.gling.bookmeup.main.ParseHelper.ClientsClass;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class BusinessClientListFragment  extends OnClickListenerFragment {
	private static final String TAG = "BusinessClientListFragment";
	
	private List<Client> _filteredClients;
	private ArrayAdapter<Client> _listViewAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		
		_filteredClients = new ArrayList<Client>();
		_listViewAdapter = new ClientsArrayAdapter();
		
		ListView listView = (ListView)view.findViewById(R.id.business_client_list_listViewClients);
		listView.setAdapter(_listViewAdapter);
		
		return view;
	}
	
	@Override
	protected int getFragmentLayoutId() {
		return R.layout.fragment_business_client_list;
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
		final ParseQuery<ParseObject> innerBusinessPointerQuery = new ParseQuery<ParseObject>(BusinessClass.CLASS_NAME).
				whereEqualTo(BusinessClass.Keys.ID, businessId);
		
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
				ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseHelper.BookingsClass.CLASS_NAME).
						whereMatchesQuery(BookingsClass.Keys.BUSINESS_POINTER, innerBusinessPointerQuery).
						whereGreaterThanOrEqualTo(BookingsClass.Keys.DATE, selectedDate).
						whereLessThan(BookingsClass.Keys.DATE, new Date()).
						whereEqualTo(BookingsClass.Keys.IS_APPROVED, true);
				query.include(BookingsClass.Keys.CLIENT_POINTER);
				
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
				_filteredClients.clear();
				for (ParseObject parseObject : objects) {
					Client client = new Client(parseObject);
					if (!_filteredClients.contains(client)) {
						_filteredClients.add(new Client(parseObject));
					}
				}
				
				_listViewAdapter.notifyDataSetChanged();
			}
		});
	}
	
	private static class Client {
		public final String _id, _clientName;

		public Client(ParseObject bookingParseObject) {
			ParseObject clientParseObject = bookingParseObject.getParseObject(BookingsClass.Keys.CLIENT_POINTER);
			_id = clientParseObject.getObjectId();
			_clientName = clientParseObject.getString(ClientsClass.Keys.NAME);
		}

		@Override
		public String toString() {
			return _clientName;
		}
		
		@Override
		public boolean equals(Object other) {
			return !(other instanceof Client) || (_id.equals(((Client)other)._id)); 
		}
	}
	
	private class ClientsArrayAdapter extends ArrayAdapter<Client> {
		public ClientsArrayAdapter() {
			super(getActivity(), R.layout.client_list_item, _filteredClients);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflator = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (convertView == null) {
				convertView = inflator.inflate(R.layout.client_list_item, null);
			}
			
			Client client = _filteredClients.get(position);
			
			TextView clientNameTextView = (TextView) convertView.findViewById(R.id.client_list_item_txtClientName);
			
			clientNameTextView.setText(client._clientName);
			
			return convertView;
		}
	}
}
