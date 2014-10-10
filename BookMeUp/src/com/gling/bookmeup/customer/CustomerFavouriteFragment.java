package com.gling.bookmeup.customer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;

import com.gling.bookmeup.R;
import com.gling.bookmeup.business.Business;
import com.gling.bookmeup.main.OnClickListenerFragment;
import com.parse.ParseException;
import com.parse.ParseObject;

public class CustomerFavouriteFragment extends OnClickListenerFragment implements TextWatcher {

	private static final String TAG = "CustomerMainActivity";
	private List<Business> _allBusinesses, _filteredBusinesses;
	private BusinessesArrayAdapter _businessesListViewAdapter;
	ListView servicesListView = null;

	@Override
	protected int getFragmentLayoutId() {
		return R.layout.customer_favourites_fragment;
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		
//		//TODO: delete
//		ParseQuery<Customer> customerQuery = new ParseQuery<Customer>(Customer.CLASS_NAME);
//		customerQuery.whereEqualTo(Customer.Keys.NAME, "gefen");
//		customerQuery.include(Customer.Keys.FAVOURITES);
//		try {
//			List<Customer> customers = customerQuery.find();
//			for (int i = 0; i < customers.size(); i++) {
//				_customer = customers.get(i);
//			}
//		} catch (ParseException e1) {
//		}
//		//TODO: delete

		_allBusinesses = new ArrayList<Business>();
		_filteredBusinesses = new ArrayList<Business>();
		_businessesListViewAdapter = new BusinessesArrayAdapter();


		@SuppressWarnings("unchecked")
		ArrayList<ParseObject> favouriteBusinesses = (ArrayList<ParseObject>) Customer.getCurrentCustomer().get(Customer.Keys.FAVOURITES);
		for (ParseObject parseObject : favouriteBusinesses) {
			if (parseObject instanceof Business) {
				Business businessItem = (Business) parseObject;
				try {
					ParseObject businessObject = businessItem.fetchIfNeeded();
					businessItem = (Business) businessObject;
					_allBusinesses.add(businessItem);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		_filteredBusinesses.addAll(_allBusinesses);
		_businessesListViewAdapter.notifyDataSetChanged();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView");

		View view = super.onCreateView(inflater, container, savedInstanceState);
		
		Log.i(TAG, "list view is initialized");
		servicesListView = (ListView)view.findViewById(R.id.customer_business_services_dialog_listview);

		final ListView businessesListView = (ListView)view.findViewById(R.id.customer_favourites_business_list_listViewBusinesses);
		businessesListView.setAdapter(_businessesListViewAdapter);
		businessesListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
				Business business = (Business) businessesListView.getItemAtPosition(position);
				Log.i(TAG, "business: " + business.getName());
				CustomerChooseBusinessDialogs dialog = new CustomerChooseBusinessDialogs();
				dialog.createBusinessProfileDialog(business, getActivity(), getResources(), Customer.getCurrentCustomer());
            }

			
        });

		EditText edtSearch = (EditText)view.findViewById(R.id.customer_favourites_business_list_edtSearch);
		edtSearch.addTextChangedListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
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
		_businessesListViewAdapter.getFilter().filter(s);
	}
	
	private class BusinessesArrayAdapter extends ArrayAdapter<Business> {

		private BusinessFilter _businessFilter;

		public BusinessesArrayAdapter() {
			super(getActivity(), R.layout.customer_business_list_item, _filteredBusinesses);
			_businessFilter = new BusinessFilter();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflator = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (convertView == null) {
				convertView = inflator.inflate(R.layout.customer_business_list_item, null);
			}

			final Business business = _filteredBusinesses.get(position);

			TextView clientNameTextView = (TextView) convertView.findViewById(R.id.business_list_item_txtBusinessName);
			TextView totalSepndingsTextView = (TextView) convertView.findViewById(R.id.business_list_item_txtBusinessType);
			
			clientNameTextView.setText(business.getName());
			totalSepndingsTextView.setText(business.getCategory().getName());

			return convertView;
		}

		@Override
		public Filter getFilter() {
			Log.i(TAG, "getFilter");
			return _businessFilter;
		}
	}

	private class BusinessFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			Log.i(TAG, "performFiltering(" + constraint + ")");

			FilterResults results = new FilterResults();

			_filteredBusinesses.clear();
			if (constraint != null && !constraint.equals("")) {
				for (Business business : _allBusinesses) {
					if (doesSetisfyConstraint(business, constraint)) {
						_filteredBusinesses.add(business);
					}
				}
			} else {
				_filteredBusinesses.addAll(_allBusinesses);
			}

			results.values = _filteredBusinesses;
			results.count = _filteredBusinesses.size();

			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			Log.i(TAG, "publicResults");
			_businessesListViewAdapter.notifyDataSetChanged();
		}

		private boolean doesSetisfyConstraint(Business business, CharSequence constraint) {
			return (constraint == null) || business.getName().toLowerCase(Locale.getDefault()).contains(constraint.toString().toLowerCase(Locale.getDefault()));
		}
	}
}
