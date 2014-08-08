package com.gling.bookmeup.business.fragments;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gling.bookmeup.R;
import com.gling.bookmeup.main.OnClickListenerFragment;
import com.gling.bookmeup.main.ParseHelper;
import com.gling.bookmeup.main.ParseHelper.BookingClass;
import com.gling.bookmeup.main.ParseHelper.BusinessClass;
import com.gling.bookmeup.main.ParseHelper.CustomerClass;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class BusinessCustomersListFragment  extends OnClickListenerFragment {
	private static final String TAG = "BusinessClientListFragment";
	
	private List<Client> _allClients, _filteredClients;
	private ClientsArrayAdapter _listViewAdapter;
	
	
	// TODO: Temporary! The businessId should be saved in the shared preferences during the profile creation. 
	private static final String BUSINESS_ID = "mUhs7IdMT7"; 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		
		_allClients = new ArrayList<Client>();
		_filteredClients = new ArrayList<Client>();
		_listViewAdapter = new ClientsArrayAdapter();
		
		final ParseQuery<ParseObject> innerBusinessPointerQuery = new ParseQuery<ParseObject>(BusinessClass.CLASS_NAME).
				whereEqualTo(BusinessClass.Keys.ID, BUSINESS_ID);
		
		/*
		 * Build a query that represents bookings with the following properties:
		 * 		For this business
		 * 		Later than the selected date
		 * 		Before today
		 *		Were approved
		 */
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(BookingClass.CLASS_NAME).
				whereMatchesQuery(BookingClass.Keys.BUSINESS_POINTER, innerBusinessPointerQuery).
				whereLessThan(BookingClass.Keys.DATE, new Date()).
				whereEqualTo(BookingClass.Keys.IS_APPROVED, true);
		query.include(BookingClass.Keys.CUSTOMER_POINTER);
		
		final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...");
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				progressDialog.dismiss();
				if (e != null) {
					Log.e(TAG, "Exception: " + e.getMessage());
					return;
				}
				
				for (ParseObject bookingParseObject : objects) {
					Client currentClient = new Client(bookingParseObject);
					int index = _allClients.indexOf(currentClient);
					if (index == -1) {
						_allClients.add(currentClient);
					} else {
						_allClients.get(index).notifyBooking(bookingParseObject);
					}
				}
				
				_filteredClients.addAll(_allClients);
				_listViewAdapter.notifyDataSetChanged();
			}
		});
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView");

		View view = super.onCreateView(inflater, container, savedInstanceState);
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
		case R.id.business_client_list_btnSendMessage:
			handleSendMessageToSelectedClients();
			break;
		case R.id.business_client_list_btnSendOffer:
			Toast.makeText(getActivity(), "Not implemeted", Toast.LENGTH_SHORT).show();
			break;
		}
	}
	
	private void handleLastVisitFilter() {
		Log.i(TAG, "handleSpendingFilter");
		
		Calendar today = Calendar.getInstance();
		// TODO: We should implement our own DatePickerDialog because this implementation is always localized (According to the localization device's preferences)
		DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				Log.i(TAG, "Date selected");
				
				// Get a Date instance that represents the date that was selected by the user
				GregorianCalendar selectedDateCalendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
				Date selectedDate = selectedDateCalendar.getTime();
				
				_listViewAdapter._clientFilter.filterByLastVisit(selectedDate);
			}
		}, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DATE));
		datePickerDialog.show();
	}
	
	private void handleSpendingsFilter() {
		Log.i(TAG, "handleSpendingsFilter");

		Toast.makeText(getActivity(), "Not implemented", Toast.LENGTH_SHORT).show();
	}
	
	private void handleSendMessageToSelectedClients() {
		Log.i(TAG, "handleSendMessageToSelectedClients");
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		LayoutInflater inflater = getActivity().getLayoutInflater();
		final View view = inflater.inflate(R.layout.business_send_message_dialog, null);
		builder.setView(view);
		
		// Set up the buttons
		builder.setPositiveButton(R.string.business_client_list_send_message_dialog_btnSend, new DialogInterface.OnClickListener() { 
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	Log.i(TAG, "Sending message to selected clients");
		    	String message = ((TextView)view.findViewById(R.id.business_client_list_send_message_dialog_edtMessage)).getText().toString();
		    	
		    	// Build a list of the ids of all selected clients
		    	List<String> clientsIds = new ArrayList<String>();
				for (Client client : _filteredClients) {
					if (client._isSelected) {
						clientsIds.add(client._id);
					}
				}
				
				// Call the back end function
				ParseHelper.BackEndFunctions.SendMessageToClients.callInBackground(BUSINESS_ID, clientsIds, message, new FunctionCallback<String>() {
					@Override
					public void done(String object, ParseException e) {
						Log.i(TAG, "callFunctionInBackground done");
						
						if (e != null) {
							Log.e(TAG, "Exception: " + e.getMessage());
							return;
						}
					}
				});
		    }
		});
		builder.setNegativeButton(R.string.business_client_list_send_message_dialog_btnCancel, new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.cancel();
		    }
		});
		builder.show();
	}

	private static class Client {
		public final String _id, _clientName;
		public Date _lastVisit;
		public int _totalSpendings;
		public boolean _isSelected;

		/*
		 * Creates a Client instance out of a Bookings record.
		 */
		public Client(ParseObject bookingParseObject) {
			ParseObject clientParseObject = bookingParseObject.getParseObject(BookingClass.Keys.CUSTOMER_POINTER);
			_id = clientParseObject.getObjectId();
			_clientName = clientParseObject.getString(CustomerClass.Keys.NAME);
			_lastVisit = bookingParseObject.getDate(BookingClass.Keys.DATE);
			_totalSpendings = 0; // TODO: Calculate spendings in booking according to services and prices
			_isSelected = false;
		}
		
		/*
		 * Notifies the Client instance about another booking.
		 * This function will summarize the total spending of the client, set the date of his last visit etc.
		 */
		public void notifyBooking(ParseObject bookingParseObject) {
			ParseObject clientParseObject = bookingParseObject.getParseObject(BookingClass.Keys.CUSTOMER_POINTER);
			if (!_id.equals(clientParseObject.getObjectId())) {
				// TODO: Handle error
				return;
			}
			
			Date bookingDate = bookingParseObject.getDate(BookingClass.Keys.DATE);
			if (bookingDate.after(_lastVisit)) {
				_lastVisit = bookingDate;
			}
			
			// TODO: Calculate spendings in booking according to services and prices
			_totalSpendings += 0;
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
		
		private ClientsFilter _clientFilter;
		
		public ClientsArrayAdapter() {
			super(getActivity(), R.layout.business_customer_list_item, _filteredClients);
			_clientFilter = new ClientsFilter();
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflator = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (convertView == null) {
				convertView = inflator.inflate(R.layout.business_customer_list_item, null);
			}
			
			final Client client = _filteredClients.get(position);
			
			TextView clientNameTextView = (TextView) convertView.findViewById(R.id.client_list_item_txtClientName);
			TextView totalSepndingsTextView = (TextView) convertView.findViewById(R.id.client_list_item_txtTotalSpent);
			CheckBox checkBoxView = (CheckBox)convertView.findViewById(R.id.client_list_item_chkBox);
			checkBoxView.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					client._isSelected = isChecked;
				}
			});
			
			clientNameTextView.setText(client._clientName);
			totalSepndingsTextView.setText(client._totalSpendings + " NIS");
			
			return convertView;
		}
		
		@Override
		public Filter getFilter() {
			Log.i(TAG, "getFilter");
			return _clientFilter;
		}
	}
	
	private class ClientsFilter extends Filter {
		/*
		 *  Optional
		 *  Should be null if the user doesn't want to filter by the date of the last visit.
		 */
		private Date _dateOfLastVisit;
		
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			Log.i(TAG, "performFiltering(" + constraint + ")");
			
			FilterResults results = new FilterResults();
			
			_filteredClients.clear();
			for (Client client : _allClients) {
				if (_dateOfLastVisit != null) {
					if (client._lastVisit.after(_dateOfLastVisit)) {
						_filteredClients.add(client);
					}
				}
			}
			
			results.values = _filteredClients;
			results.count = _filteredClients.size();
			
			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			Log.i(TAG, "publicResults");
			_listViewAdapter.notifyDataSetChanged();
		}
		
		private void filterByLastVisit(Date date) {
			_dateOfLastVisit = date;
			filter(null);
		}
		
		private void unfilterByLastVisit() {
			_dateOfLastVisit = null;
		}
	}
}