package com.gling.bookmeup.customer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.gling.bookmeup.main.ParseHelper;
import com.gling.bookmeup.main.ParseHelper.Category;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

public class CustomerPopularFragment extends OnClickListenerFragment implements /*OnClickListener,*/ TextWatcher {

	private static final String TAG = "CustomerMainActivity";

	private List<Business> _allBusinesses, _filteredBusinesses;
	private BusinessesArrayAdapter _businessesListViewAdapter;
	private ArrayAdapter<String> _categoriesAdapter;
	ListView servicesListView = null;
	private Customer _customer;

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

//		_customer = ((CustomerMainActivity)getActivity()).getCustomer();
		
		_allBusinesses = new ArrayList<Business>();
		_filteredBusinesses = new ArrayList<Business>();
		_businessesListViewAdapter = new BusinessesArrayAdapter();


		ParseQuery<Business> getBusinessQuery = new ParseQuery<Business>(Business.CLASS_NAME);
		getBusinessQuery.include(Business.Keys.CATEGORY);

		final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...");
		getBusinessQuery.findInBackground(new FindCallback<Business>() {
			@Override
			public void done(List<Business> objects, ParseException e) {
				progressDialog.dismiss();
				if (e != null) {
					Log.e(TAG, "Exception: " + e.getMessage());
					return;
				}

				for (Business business : objects) {
					int index = _allBusinesses.indexOf(business);
					if (index == -1) {
						_allBusinesses.add(business);
					} else {
					}
				}


				_businessesListViewAdapter.notifyDataSetChanged();
			}
		});
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
    	Set<String> categorySet = new HashSet<String>();
    	categorySet = sp.getStringSet(ParseHelper.BUSINESS_CATEGORIES, categorySet);
    	String[] categoryArr = categorySet.toArray(new String[categorySet.size()]);
    	_categoriesAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_single_choice, categoryArr);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView");
		
		View view = super.onCreateView(inflater, container, savedInstanceState);
		
		Log.i(TAG, "list view is initialized");
		servicesListView = (ListView)view.findViewById(R.id.customer_business_services_dialog_listview);

		final ListView businessesListView = (ListView)view.findViewById(R.id.customer_business_list_listViewBusinesses);
		businessesListView.setAdapter(_businessesListViewAdapter);
		businessesListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
				Business business = (Business) businessesListView.getItemAtPosition(position);
				Log.i(TAG, "business: " + business.getName());
				_customer = ((CustomerMainActivity)getActivity()).getCustomer();
				CustomerChooseBusinessDialogs dialog = new CustomerChooseBusinessDialogs();
				dialog.createBusinessProfileDialog(business, getActivity(), getResources(), _customer);
            }

			
        });

		EditText edtSearch = (EditText)view.findViewById(R.id.customer_business_list_edtSearch);
		edtSearch.addTextChangedListener(this);

		final ListView categoriesListView = (ListView)view.findViewById(R.id.customer_business_list_listViewBusinessesCategories);
		categoriesListView.setAdapter(_categoriesAdapter);
		categoriesListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
				String category = _categoriesAdapter.getItem(position);
				_businessesListViewAdapter.getBusinessFilter().filterByCatagory(category);
            }

			
        });
		
		return view;
	}
	
	@Override
	protected int getFragmentLayoutId() {
		return R.layout.customer_popular_fragment;
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
		//		}
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
			if (business.getCategory() != null) {
				totalSepndingsTextView.setText(business.getCategory());
			} else {
				totalSepndingsTextView.setText("No Category");
			}

			return convertView;
		}

		@Override
		public Filter getFilter() {
			Log.i(TAG, "getFilter");
			return _businessFilter;
		}
		
		public BusinessFilter getBusinessFilter() {
			Log.i(TAG, "getBusinessFilter");
			return _businessFilter;
		}
	}

	private class BusinessFilter extends Filter {
		private String _chosenCategory;

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
			} else if(_chosenCategory != null) {
				for (Business business : _allBusinesses) {
					if (doesSetisfyCategory(business)) {
						_filteredBusinesses.add(business);
					}
				}
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

		public void filterByCatagory(String category) {
			_chosenCategory = category;
			filter(null);
		}

		private boolean doesSetisfyConstraint(Business business, CharSequence constraint) {
			return (constraint == null) || business.getName().toLowerCase(Locale.getDefault()).contains(constraint.toString().toLowerCase(Locale.getDefault()));
		}
		
		private boolean doesSetisfyCategory(Business business) {
			String categoryName;
			if ((categoryName = business.getCategory()) != null) {
				return (_chosenCategory == null) || _chosenCategory.equals(categoryName);
			}
			return false;
		}
	}
	
}
