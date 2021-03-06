package com.gling.bookmeup.business;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.Card.OnLongCardClickListener;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardHeader.OnClickCardHeaderOtherButtonListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.gling.bookmeup.main.Constants;
import com.gling.bookmeup.main.GenericMultiChoiceCardArrayAdapter;
import com.gling.bookmeup.main.ICardGenerator;
import com.gling.bookmeup.main.IObservableList;
import com.gling.bookmeup.main.ObservableArrayList;
import com.gling.bookmeup.main.OnClickListenerFragment;
import com.gling.bookmeup.main.PushUtils;
import com.gling.bookmeup.main.views.BaseListViewWrapperView.DisplayMode;
import com.gling.bookmeup.main.views.CardListViewWrapperView;
import com.gling.bookmeup.sharedlib.parse.Business;
import com.gling.bookmeup.sharedlib.parse.Customer;
import com.gling.bookmeup.sharedlib.parse.ParseHelper.Booking;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SendCallback;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class BusinessCustomersListFragment  extends OnClickListenerFragment implements TextWatcher, OnMenuItemClickListener
{
	private static final String TAG = "BusinessCustomersListFragment";

	private IObservableList<CustomerForBusiness> _allCustomers, _filteredCustomers;
	private CustomerCardArrayMultiChoiceAdapter _customerCardsAdapter;
	
	private CardListViewWrapperView _customerCardListView;

	private EditText _edtSearch;
	private PopupMenu _popupMenuFilter;
	private ImageButton _imgViewBtnFilter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView");

		_allCustomers = new ObservableArrayList<CustomerForBusiness>();
		_filteredCustomers = new ObservableArrayList<CustomerForBusiness>();
		
		_customerCardsAdapter = new CustomerCardArrayMultiChoiceAdapter(_filteredCustomers, new CustomerCardGenerator(), R.menu.business_customer_list_mutlichoice);
		
		View view = super.onCreateView(inflater, container, savedInstanceState);
		_customerCardListView = (CardListViewWrapperView) view.findViewById(R.id.business_customer_list_listViewCustomers);
		_customerCardListView.setAdapter(_customerCardsAdapter);
		_customerCardListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		
		_edtSearch = (EditText)view.findViewById(R.id.business_customer_list_edtSearch);
		_edtSearch.addTextChangedListener(this);
		
		_imgViewBtnFilter = (ImageButton) view.findViewById(R.id.business_customer_list_btnFilter);
		_imgViewBtnFilter.setOnClickListener(this);
		
		_popupMenuFilter = new PopupMenu(getActivity(), _imgViewBtnFilter);
		_popupMenuFilter.inflate(R.menu.business_customer_list_filter);
		_popupMenuFilter.setOnMenuItemClickListener(this);
		
		/*
		 * Build a query that represents bookings with the following properties:
		 * 		For this business
		 * 		Later than the selected date
		 * 		Before today
		 *		Were approved
		 */
		ParseQuery<Booking> query = new ParseQuery<Booking>(Booking.CLASS_NAME).
				whereEqualTo(Booking.Keys.BUSINESS_POINTER, Business.getCurrentBusiness()).
				whereEqualTo(Booking.Keys.STATUS, Booking.Status.APPROVED);
		query.include(Booking.Keys.CUSTOMER_POINTER);
		query.include(Booking.Keys.SERVICE_POINTER);
		
		_customerCardListView.setDisplayMode(DisplayMode.LOADING_VIEW);
		query.findInBackground(new FindCallback<Booking>() {
			@Override
			public void done(List<Booking> objects, ParseException e) {
				if (e != null) {
					Log.e(TAG, "Exception: " + e.getMessage());
					return;
				}
				
				for (Booking bookingParseObject : objects) {
					CustomerForBusiness currentCustomer = new CustomerForBusiness(bookingParseObject);
					int indexOfCustomer = _allCustomers.indexOf(currentCustomer);
					if (indexOfCustomer == -1)
					{
						_allCustomers.add(currentCustomer);
					}
					else
					{
						_allCustomers.get(indexOfCustomer).notifyBooking(bookingParseObject);
					}
				}
				
				_filteredCustomers.addAll(_allCustomers);
				_customerCardsAdapter.notifyDataSetChanged();
				
				DisplayMode newDisplayMode = _allCustomers.isEmpty()? DisplayMode.NO_ITEMS_VIEW : DisplayMode.LIST_VIEW;  
				_customerCardListView.setDisplayMode(newDisplayMode);
			}
		});
		
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
		case R.id.business_customer_list_btnFilter:
			Log.i(TAG, "btnFilter clicked");
			_popupMenuFilter.show();
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
		
		CustomersFilter filter = _customerCardsAdapter._customersFilter;
		
		_imgViewBtnFilter.setActivated(!s.toString().isEmpty() || filter._dateOfLastVisit != null || filter._spendingsLimit != null);
		_customerCardsAdapter.getFilter().filter(s);
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
				
				_customerCardsAdapter._customersFilter.filterByLastVisit(selectedDate);
				_imgViewBtnFilter.setActivated(true);
			}
		}, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DATE));
		datePickerDialog.show();
	}
	
	private void handleSpendingsFilter() {
		Log.i(TAG, "handleSpendingsFilter");

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		LayoutInflater inflater = getActivity().getLayoutInflater();
		final View view = inflater.inflate(R.layout.business_customer_list_spendings_filter_dialog, null);
		builder.setView(view);
		
		builder.setTitle(R.string.business_customer_list_spendings_filter_dialog_title);
		
		Button btnCancel = (Button) view.findViewById(R.id.business_customer_list_spendings_filter_dialog_btnCancel);
		Button btnOk = (Button) view.findViewById(R.id.business_customer_list_spendings_filter_dialog_btnOk);
		final EditText edtSpendings = (EditText) view.findViewById(R.id.business_customer_list_spendings_filter_dialog_edtSpendings);
		
		final AlertDialog dialog = builder.create();
		btnCancel.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View view) 
			{
				Log.i(TAG, "btnCancel click");
				dialog.cancel();
			}
		});
		btnOk.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View view) 
			{
				Log.i(TAG, "btnOk click");
		    	String spendingLimitInput = edtSpendings.getText().toString();
		    	if (spendingLimitInput.isEmpty()) {
		    		edtSpendings.setError("Please insert a value");
		    		return;
		    	}
		    	
		    	dialog.dismiss();
		    	int spendingsLimit = Integer.parseInt(spendingLimitInput);
		    	_customerCardsAdapter._customersFilter.filterBySpendings(spendingsLimit);
		    	_imgViewBtnFilter.setActivated(true);
			}
		});
		
		dialog.show();
	}
	
	private void handleClearFilter() 
	{
		_customerCardsAdapter._customersFilter.clearFilters();
		_edtSearch.setText("");
		_customerCardsAdapter.notifyDataSetChanged();
	}
	
	private void handleSendMessageToSelectedClients() {
		Log.i(TAG, "handleSendMessageToSelectedClients");
		
		List<CustomerForBusiness> selectedCustomers = _customerCardsAdapter.getSelectedItems();
		if (selectedCustomers.isEmpty()) {
			Crouton.showText(getActivity(), "Please select customers from the list", Style.ALERT);
			return;
		}
		
		final List<String> selectedCustomerIds = new ArrayList<String>();
		for (CustomerForBusiness customer : selectedCustomers)
		{
			selectedCustomerIds.add(customer._id);
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		LayoutInflater inflater = getActivity().getLayoutInflater();
		final View view = inflater.inflate(R.layout.business_customer_list_send_message_dialog, null);
		builder.setView(view);
		
		// Set up the buttons
		builder.setPositiveButton(R.string.business_customer_list_send_message_dialog_btnSend, new DialogInterface.OnClickListener() { 
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	Log.i(TAG, "Sending message to selected clients");
		    	String message = ((TextView)view.findViewById(R.id.business_client_list_send_message_dialog_edtMessage)).getText().toString();
		    	
		    	PushUtils.sendMessageToCustomers(Business.getCurrentBusiness().getObjectId(), Business.getCurrentBusiness().getName(), selectedCustomerIds, message, new SendCallback() {
					@Override
					public void done(ParseException e) {
						Log.i(TAG, "sendMessageToCustomers done");
						
						if (e != null) {
							Log.e(TAG, "Exception: " + e.getMessage());
							return;
						}
					}
				});
		    }
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.cancel();
		    }
		});
		builder.show();
	}
	
	private void handleSendOfferToSelectedClients() {
		Log.i(TAG, "handleSendOfferToSelectedClients");
		
		List<CustomerForBusiness> selectedCustomers = _customerCardsAdapter.getSelectedItems();
		if (selectedCustomers.isEmpty()) {
			Crouton.showText(getActivity(), "Please select customers from the list", Style.ALERT);
			return;
		}
		
		final List<String> selectedCustomerIds = new ArrayList<String>();
		for (CustomerForBusiness customer : selectedCustomers)
		{
			selectedCustomerIds.add(customer._id);
		}
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		final View view = inflater.inflate(R.layout.business_customer_list_send_offer_dialog , null);
		
		// TODO: Put the possible discount in an int array resource.
		Integer[] validDiscounts = {5, 10, 15, 20};
    	SpinnerAdapter discountSpinnerAdapter = new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_spinner_item, validDiscounts);
    	final Spinner discountSpinner = (Spinner)view.findViewById(R.id.business_customer_list_send_offer_dialog_spinnerDiscount);
    	discountSpinner.setAdapter(discountSpinnerAdapter);
    	
    	Integer[] validDurations = {1, 2, 3, 4};
    	SpinnerAdapter durationSpinnerAdapter = new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_spinner_item, validDurations);
    	final Spinner durationSpinner = (Spinner)view.findViewById(R.id.business_customer_list_send_offer_dialog_spinnerDuration);
    	durationSpinner.setAdapter(durationSpinnerAdapter);
    	
    	// Build an alert dialog
    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(view);
		builder.setTitle(R.string.business_customer_list_send_offer_dialog_title);
		builder.setPositiveButton(R.string.business_customer_list_send_message_dialog_btnSend, new DialogInterface.OnClickListener() { 
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	Log.i(TAG, "Sending offer to selected clients");

		    	final int discount = (Integer) discountSpinner.getSelectedItem();
		    	final int duration = (Integer) durationSpinner.getSelectedItem();
		    	PushUtils.sendOfferToCustomers(Business.getCurrentBusiness().getObjectId(), Business.getCurrentBusiness().getName(), selectedCustomerIds, discount, duration, new SendCallback() {
					
					@Override
					public void done(ParseException e) {
						Log.i(TAG, "sendOfferToCustomers done");

						if (e != null) {
							Log.e(TAG, "Exception: " + e.getMessage());
							return;
						}
					}
				});
		    }
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.cancel();
		    }
		});
		builder.show();
	}
	
	private static class CustomerForBusiness {
		public final String _id, _customerName, _phoneNumber;
		public Date _lastVisit;
		public int _totalSpendings;

		/*
		 * Creates a Client instance out of a Bookings record.
		 */
		public CustomerForBusiness(Booking booking) {
			Customer customer = booking.getCustomer();
			_id = customer.getObjectId();
			_customerName = customer.getName();
			_phoneNumber = customer.getPhoneNumber();
			_lastVisit = booking.getDate();
			_totalSpendings = booking.getServicePrice();
		}
		
		/*
		 * Notifies the Client instance about another booking.
		 * This function will summarize the total spending of the client, set the date of his last visit etc.
		 */
		public void notifyBooking(Booking booking) {
			Log.i(TAG, "notifyBooking");
			
			ParseObject customerParseObject = booking.getParseObject(Booking.Keys.CUSTOMER_POINTER);
			if (!_id.equals(customerParseObject.getObjectId())) {
				// TODO: Handle error
				return;
			}
			
			Date bookingDate = booking.getDate(Booking.Keys.DATE);
			if (bookingDate.after(_lastVisit)) {
				_lastVisit = bookingDate;
			}
			
			_totalSpendings += booking.getServicePrice();
		}

		@Override
		public String toString() {
			return _customerName;
		}
		
		@Override
		public boolean equals(Object other) {
			return !(other instanceof CustomerForBusiness) || (_id.equals(((CustomerForBusiness)other)._id)); 
		}
	}
	
	private class CustomersFilter extends Filter {
		/*
		 *  Optional
		 *  Should be null if the user doesn't want to filter by the date of the last visit.
		 */
		private Date _dateOfLastVisit;
		
		/*
		 *  Optional
		 *  Should be null if the user doesn't want to filter by total spendings.
		 */
		private Integer _spendingsLimit;
		
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			Log.i(TAG, "performFiltering(" + constraint + ")");
			
			FilterResults results = new FilterResults();
			
			_filteredCustomers.clear();
			for (CustomerForBusiness customer : _allCustomers) {
				if (doesSetisfyLastVisitFilter(customer) &&
						doestSetisfySpendingsFilter(customer) &&
						doesSetisfyConstraint(customer, constraint)) {
					_filteredCustomers.add(customer);
				}
			}
			
			results.values = _filteredCustomers;
			results.count = _filteredCustomers.size();
			
			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			Log.i(TAG, "publicResults. results.count = " + results.count);
			if (_filteredCustomers.isEmpty())
			{
				_customerCardListView.setDisplayMode(DisplayMode.NO_ITEMS_VIEW);
			}
			else
			{
				_customerCardListView.setDisplayMode(DisplayMode.LIST_VIEW);
			}
			_customerCardsAdapter.notifyDataSetChanged();
		}
		
		private void filterByLastVisit(Date date) {
			_dateOfLastVisit = date;
			filter(null);
		}
		
		private void filterBySpendings(int spendingsLimit) {
			Log.i(TAG, "filterBySpendings. Limit = " + spendingsLimit);
			
			_spendingsLimit = spendingsLimit;
			filter(null);
		}
		
		public void clearFilters()
		{
			_dateOfLastVisit = null;
			_spendingsLimit = null;
			
			filter("");
		}
		
		private boolean doesSetisfyLastVisitFilter(CustomerForBusiness customer) {
			return (_dateOfLastVisit == null) || customer._lastVisit.after(_dateOfLastVisit);
		}
		
		private boolean doestSetisfySpendingsFilter(CustomerForBusiness customer) {
			Log.i(TAG, "doestSetisfySpendingsFilter. customer spendings = " + customer._totalSpendings + ", limit = " + _spendingsLimit);
			return (_spendingsLimit == null) || (customer._totalSpendings >= _spendingsLimit);
		}
		
		private boolean doesSetisfyConstraint(CustomerForBusiness customer, CharSequence constraint) {
			return (constraint == null) || customer._customerName.toLowerCase().contains(constraint.toString().toLowerCase());
		}
	}
	
	public class CustomerCardArrayMultiChoiceAdapter extends GenericMultiChoiceCardArrayAdapter<CustomerForBusiness> {
		
		private CustomersFilter _customersFilter;
		
		public CustomerCardArrayMultiChoiceAdapter(IObservableList<CustomerForBusiness> items, ICardGenerator<CustomerForBusiness> cardFactory, int menuRes) 
		{
			super(getActivity(), items, cardFactory, menuRes);
			_customersFilter = new CustomersFilter();
		}

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        	switch (item.getItemId())
        	{
        	case R.id.busienss_customer_list_action_bar_menu_send_offer:
        		handleSendOfferToSelectedClients();
        		mode.finish();
        		return true;
        	}
        	
        	return false;
        }

        @Override
        public Filter getFilter() {
        	return _customersFilter;
        }
    }
	
	private class CustomerCardGenerator implements ICardGenerator<CustomerForBusiness>
	{
		@Override
		public Card generateCard(CustomerForBusiness customer) 
		{
			return new CustomerCard(getActivity(), customer, new OnLongCardClickListener() 
			{
				@Override
				public boolean onLongClick(Card card, View view) 
				{
					return _customerCardsAdapter.startActionMode(getActivity());
				}
			});
		}
	}
	
	private static class CustomerCard extends Card
	{
		private final CustomerForBusiness _customer;
		
		public CustomerCard(final Context context, CustomerForBusiness customer, OnLongCardClickListener onLongCardClickListener) 
		{
			super(context, R.layout.business_customer_list_customer_card);
			
			_customer = customer;
			
			CardHeader header = new CardHeader(context);
			header.setTitle(_customer._customerName);
			addCardHeader(header);
			
			final String phoneNumber = _customer._phoneNumber;
			if (phoneNumber != null)
			{
				header.setOtherButtonDrawable(R.drawable.btn_action_call);
				header.setOtherButtonVisible(true);
				header.setOtherButtonClickListener(new OnClickCardHeaderOtherButtonListener() 
				{
					@Override
					public void onButtonItemClick(Card card, View view) 
					{
						Log.i(TAG, "btn_action_call clicked");
						Intent callIntent = new Intent(Intent.ACTION_CALL);
						callIntent.setData(Uri.parse("tel:" + phoneNumber));
						context.startActivity(callIntent);
					}
				});
			}
			
			setId(customer._id);
			setOnLongClickListener(onLongCardClickListener);
			setBackgroundResourceId(R.drawable.customer_business_card_selector);
		}
		
		@Override
		public void setupInnerViewElements(ViewGroup parent, View view) 
		{
			TextView txtTotalSpendings = (TextView) view.findViewById(R.id.business_customer_list_customer_card_total_spendings);
			txtTotalSpendings.setText(_customer._totalSpendings + " NIS");
			
			TextView txtLastVisit = (TextView) view.findViewById(R.id.business_customer_list_customer_card_last_visit);
			txtLastVisit.setText(Constants.DATE_TIME_FORMAT.format(_customer._lastVisit));
		}
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) 
	{
		Log.i(TAG, "onMenuItemClick");
		
		switch (item.getItemId())
		{
		case R.id.busienss_customer_list_filter_menu_spendings:
			Log.i(TAG, "Spending filter selected");
			handleSpendingsFilter();
			break;
		case R.id.busienss_customer_list_filter_menu_last_visit:
			Log.i(TAG, "Last visit filter selected");
			handleLastVisitFilter();
			break;
		case R.id.busienss_customer_list_filter_menu_clear:
			Log.i(TAG, "Clear selected");
			handleClearFilter();
			_imgViewBtnFilter.setActivated(false);
			break;
		}
		return false;
	}
}