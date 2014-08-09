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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gling.bookmeup.R;
import com.gling.bookmeup.main.OnClickListenerFragment;
import com.gling.bookmeup.main.ParseHelper;
import com.gling.bookmeup.main.ParseHelper.Booking;
import com.gling.bookmeup.main.ParseHelper.CustomerClass;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class BusinessCustomersListFragment  extends OnClickListenerFragment implements TextWatcher {
	private static final String TAG = "BusinessCustomersListFragment";
	
	private List<Customer> _allCustomers, _filteredCustomers;
	private CustomersArrayAdapter _listViewAdapter;
	
	// TODO: Temporary! The businessId should be saved in the shared preferences during the profile creation. 
	private static final String BUSINESS_ID = "mUhs7IdMT7"; 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		
		_allCustomers = new ArrayList<Customer>();
		_filteredCustomers = new ArrayList<Customer>();
		_listViewAdapter = new CustomersArrayAdapter();
		
		final ParseQuery<ParseObject> innerBusinessPointerQuery = new ParseQuery<ParseObject>(Business.CLASS_NAME).
				whereEqualTo(Business.Keys.ID, BUSINESS_ID);
		
		/*
		 * Build a query that represents bookings with the following properties:
		 * 		For this business
		 * 		Later than the selected date
		 * 		Before today
		 *		Were approved
		 */
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(Booking.CLASS_NAME).
				whereMatchesQuery(Booking.Keys.BUSINESS_POINTER, innerBusinessPointerQuery).
				whereLessThan(Booking.Keys.DATE, new Date()).
				whereEqualTo(Booking.Keys.IS_APPROVED, true);
		query.include(Booking.Keys.CUSTOMER_POINTER);
		
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
					Customer currentCustomer = new Customer(bookingParseObject);
					int index = _allCustomers.indexOf(currentCustomer);
					if (index == -1) {
						_allCustomers.add(currentCustomer);
					} else {
						_allCustomers.get(index).notifyBooking(bookingParseObject);
					}
				}
				
				_filteredCustomers.addAll(_allCustomers);
				_listViewAdapter.notifyDataSetChanged();
			}
		});
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView");

		View view = super.onCreateView(inflater, container, savedInstanceState);
		ListView listView = (ListView)view.findViewById(R.id.business_customer_list_listViewClients);
		listView.setAdapter(_listViewAdapter);
		
		EditText edtSearch = (EditText)view.findViewById(R.id.business_customer_list_edtSearch);
		edtSearch.addTextChangedListener(this);
		
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
		case R.id.business_customer_list_btnFilterBySpendings:
			Log.i(TAG, "btnFilterBySpending clicked");
			handleSpendingsFilter();
			break;
		case R.id.business_customer_list_btnFilterByLastVisit:
			Log.i(TAG, "btnFilterByLastVisit clicked");
			handleLastVisitFilter();
			break;
		case R.id.business_customer_list_btnSendMessage:
			handleSendMessageToSelectedClients();
			break;
		case R.id.business_customer_list_btnSendOffer:
			Toast.makeText(getActivity(), "Not implemeted", Toast.LENGTH_SHORT).show();
			break;
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		Log.i(TAG, "afterTextChanged");
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		Log.i(TAG, "beforeTextChanged");		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before	, int count) {
		Log.i(TAG, "onTextChanged");
		_listViewAdapter.getFilter().filter(s);
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
		builder.setPositiveButton(R.string.business_customer_list_send_message_dialog_btnSend, new DialogInterface.OnClickListener() { 
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	Log.i(TAG, "Sending message to selected clients");
		    	String message = ((TextView)view.findViewById(R.id.business_client_list_send_message_dialog_edtMessage)).getText().toString();
		    	
		    	// Build a list of the ids of all selected clients
		    	List<String> clientsIds = new ArrayList<String>();
				for (Customer client : _filteredCustomers) {
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
		builder.setNegativeButton(R.string.business_customer_list_send_message_dialog_btnCancel, new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.cancel();
		    }
		});
		builder.show();
	}

	private static class Customer {
		public final String _id, _customerName;
		public Date _lastVisit;
		public int _totalSpendings;
		public boolean _isSelected;

		/*
		 * Creates a Client instance out of a Bookings record.
		 */
		public Customer(ParseObject bookingParseObject) {
			ParseObject clientParseObject = bookingParseObject.getParseObject(Booking.Keys.CUSTOMER_POINTER);
			_id = clientParseObject.getObjectId();
			_customerName = clientParseObject.getString(CustomerClass.Keys.NAME);
			_lastVisit = bookingParseObject.getDate(Booking.Keys.DATE);
			_totalSpendings = 0; // TODO: Calculate spendings in booking according to services and prices
			_isSelected = false;
		}
		
		/*
		 * Notifies the Client instance about another booking.
		 * This function will summarize the total spending of the client, set the date of his last visit etc.
		 */
		public void notifyBooking(ParseObject bookingParseObject) {
			ParseObject clientParseObject = bookingParseObject.getParseObject(Booking.Keys.CUSTOMER_POINTER);
			if (!_id.equals(clientParseObject.getObjectId())) {
				// TODO: Handle error
				return;
			}
			
			Date bookingDate = bookingParseObject.getDate(Booking.Keys.DATE);
			if (bookingDate.after(_lastVisit)) {
				_lastVisit = bookingDate;
			}
			
			// TODO: Calculate spendings in booking according to services and prices
			_totalSpendings += 0;
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
		
		private CustomersFilter _clientFilter;
		
		public CustomersArrayAdapter() {
			super(getActivity(), R.layout.business_customer_list_item, _filteredCustomers);
			_clientFilter = new CustomersFilter();
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflator = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (convertView == null) {
				convertView = inflator.inflate(R.layout.business_customer_list_item, null);
			}
			
			final Customer client = _filteredCustomers.get(position);
			
			TextView clientNameTextView = (TextView) convertView.findViewById(R.id.client_list_item_txtClientName);
			TextView totalSepndingsTextView = (TextView) convertView.findViewById(R.id.client_list_item_txtTotalSpent);
			CheckBox checkBoxView = (CheckBox)convertView.findViewById(R.id.client_list_item_chkBox);
			checkBoxView.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					client._isSelected = isChecked;
				}
			});
			
			clientNameTextView.setText(client._customerName);
			totalSepndingsTextView.setText(client._totalSpendings + " NIS");
			
			return convertView;
		}
		
		@Override
		public Filter getFilter() {
			Log.i(TAG, "getFilter");
			return _clientFilter;
		}
	}
	
	private class CustomersFilter extends Filter {
		/*
		 *  Optional
		 *  Should be null if the user doesn't want to filter by the date of the last visit.
		 */
		private Date _dateOfLastVisit;
		
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			Log.i(TAG, "performFiltering(" + constraint + ")");
			
			FilterResults results = new FilterResults();
			
			_filteredCustomers.clear();
			for (Customer customer : _allCustomers) {
				if (doesSetisfyLastVisitFilter(customer) && doesSetisfyConstraint(customer, constraint)) {
					_filteredCustomers.add(customer);
				}
			}
			
			results.values = _filteredCustomers;
			results.count = _filteredCustomers.size();
			
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
		
		private boolean doesSetisfyLastVisitFilter(Customer customer) {
			return _dateOfLastVisit == null || customer._lastVisit.after(_dateOfLastVisit);
		}
		
		private boolean doesSetisfyConstraint(Customer customer, CharSequence constraint) {
			return customer == null || customer._customerName.toLowerCase().contains(constraint.toString().toLowerCase());
		}
	}
}
