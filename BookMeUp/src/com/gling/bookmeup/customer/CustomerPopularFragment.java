package com.gling.bookmeup.customer;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;

import com.gling.bookmeup.R;
import com.gling.bookmeup.business.Business;
import com.gling.bookmeup.main.OnClickListenerFragment;
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
	private ParseQueryAdapter<Category> _categoriesAdapter;
	private List<Category> _allCategories;
	Button b;

	private Customer _customer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);

		_customer = new Customer();
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
						//						_allBusinesses.get(index).notifyBooking(business);
					}
				}


				//				_filteredBusinesses.addAll(_allBusinesses);
				_businessesListViewAdapter.notifyDataSetChanged();
			}
		});
		
		_categoriesAdapter = new ParseQueryAdapter<Category>(getActivity(),
				Category.CLASS_NAME);
		_categoriesAdapter.setTextKey(Category.Keys.NAME);
		/**
		_categoriesAdapter.addOnQueryLoadListener(new OnQueryLoadListener<ParseObject>() {
            public void onLoading() {
              // Trigger any "loading" UI
            }
          
            public void onLoaded(List<ParseObject> categories, Exception paramException) {
//                String categoryName = business.getCategory().getString(Category.Keys.NAME);
//                int position = 0;
//                for (int i = 0; i < categories.size(); i++) {
//                    if (categories.get(i).getString(Category.Keys.NAME).equalsIgnoreCase(categoryName)) {
//                        position = i;
//                        break;
//                    }
//                }
//
//                spnCategory.setSelection(position);
            }
          });*/

//        spnCategory.setAdapter(categoryAdapter);
        
//    }
//		ParseQuery<Category> getCategoriesQuery = new ParseQuery<Category>(Category.CLASS_NAME);
//
//		progressDialog.show();
//		getCategoriesQuery.findInBackground(new FindCallback<Category>() {
//			@Override
//			public void done(List<Category> objects, ParseException e) {
//				progressDialog.dismiss();
//				if (e != null) {
//					Log.e(TAG, "Exception: " + e.getMessage());
//					return;
//				}
//
//				for (Category category : objects) {
//					int index = _allCategories.indexOf(category);
//					if (index == -1) {
//						_allCategories.add(category);
//					} else {
//						//						_allBusinesses.get(index).notifyBooking(business);
//					}
//				}
//
//
//				//				_filteredBusinesses.addAll(_allBusinesses);
//				_categories.notifyDataSetChanged();
//			}
//		});

	}

	//	@Override
	//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	//			Bundle savedInstanceState) {
	//		Log.i(TAG, "onCreateView");
	//
	//		View rootView = inflater.inflate(R.layout.fragment_main_screen,
	//				container, false);
	//		
	//		// Set event listeners
	//		rootView.findViewById(R.id.main_screen_btnBabrbers).setOnClickListener(this);
	//		rootView.findViewById(R.id.main_screen_btnCosmetics).setOnClickListener(this);
	//
	//		return rootView;
	//	}
	//

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView");

		View view = super.onCreateView(inflater, container, savedInstanceState);
		
		final ListView businessesListView = (ListView)view.findViewById(R.id.customer_business_list_listViewBusinesses);
		businessesListView.setAdapter(_businessesListViewAdapter);
		businessesListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
				Business business = (Business) businessesListView.getItemAtPosition(position);
				Log.i(TAG, "business: " + business.getName());
				LayoutInflater inflater = getActivity().getLayoutInflater();
				final View dialogView = inflater.inflate(R.layout.customer_business_profile_dialog , null);
				
		    	final TextView nameView = (TextView)dialogView.findViewById(R.id.customer_business_profile_dialog_name);
		    	nameView.setText(business.getName());
		    	
		    	final TextView categoryView = (TextView)dialogView.findViewById(R.id.customer_business_profile_dialog_category);
		    	if (business.getCategory() != null) {
		    		categoryView.setText(business.getCategory().getName());
		    	}
		    	
		    	final TextView descriptionView = (TextView)dialogView.findViewById(R.id.customer_business_profile_dialog_description);
		    	descriptionView.setText(business.getDescription());
		    	
		    	final TextView hoursView = (TextView)dialogView.findViewById(R.id.customer_business_profile_dialog_opening_hours);
		    	hoursView.setText(business.getOpeningHours());
		    	
		    	
		    	// Build an alert dialog
		    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setView(dialogView);
				builder.setTitle(business.getName());
				
				builder.setPositiveButton(R.string.customer_business_profile_btnNextTxt, new DialogInterface.OnClickListener() { 
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				    	Log.i(TAG, "Business was chosen");
				    	
//				    	LayoutInflater inflater = getActivity().getLayoutInflater();
//						final View servicesView = inflater.inflate(R.layout.customer_business_profile_dialog , null);
//						
//						AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
//						builder1.setView(dialogView);
//						builder1.setTitle("gefen");
//						builder1.show();
				    	
				    }
				});
				builder.setNegativeButton(R.string.customer_business_profile_btnCancelTxt, new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				        dialog.cancel();
				    }
				});
				
				builder.show();
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
				ParseObject object = (ParseObject) categoriesListView.getItemAtPosition(position);
				Category category = (Category) object;
				_businessesListViewAdapter.getBusinessFilter().filterByCatagory(category);
//                Intent i = new Intent(getApplicationContext(),
//                        SingleRestraunt.class);
//                i.putExtra("restName", name);
//                startActivity(i);

            }

			
        });
		
		//		ArrayList<Button> buttons = new ArrayList<Button>();
//		b = (Button) view.findViewById(R.id.customer_main_screen_btnBabrbers);

		return view;
	}

	@Override
	protected int getFragmentLayoutId() {
		return R.layout.customer_popular_fragment;
	}

	@Override
	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.customer_main_screen_btnBabrbers:
////			_listViewAdapter._businessFilter.filterByCatagory(?);
//			Log.i(TAG, "main_screen_btnBabrbers clicked");
//			break;
//		case R.id.customer_main_screen_btnCosmetics:
//			Log.i(TAG, "main_screen_btnCosmetics clicked");
//			break;
//		case R.id.customer_main_screen_btnDentists:
//			Log.i(TAG, "btnDentists clicked");
//			break;
//		case R.id.customer_main_screen_btnGarages:
//			Log.i(TAG, "btnGarages clicked");
//			break;
//		}
//
//		Toast.makeText(getActivity(), "Not implemented", Toast.LENGTH_SHORT).show();
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
		//		if ((s == null) || (s == "")) {
		//			_filteredBusinesses.clear();
		//			_listViewAdapter.notifyDataSetChanged();
		//		} else {
		if (s == null || s.equals("")) {
			b.setVisibility(View.GONE);
		}
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
				totalSepndingsTextView.setText(business.getCategory().getName());
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
	
//	private class CategoriesArrayAdapter extends ArrayAdapter<Category> {
//
//
//		public CategoriesArrayAdapter() {
//			super(getActivity(), R.layout.customer_category_list_item, _allCategories);
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			LayoutInflater inflator = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//			if (convertView == null) {
//				convertView = inflator.inflate(R.layout.customer_business_list_item, null);
//			}
//
//			final Business business = _filteredBusinesses.get(position);
//
//			TextView clientNameTextView = (TextView) convertView.findViewById(R.id.business_list_item_txtBusinessName);
//			TextView totalSepndingsTextView = (TextView) convertView.findViewById(R.id.business_list_item_txtBusinessType);
//
//			clientNameTextView.setText(business.getName());
//			totalSepndingsTextView.setText("Default Type");
//
//			return convertView;
//		}
//	}

	private class BusinessFilter extends Filter {
		/*
		 *  Optional
		 *  Should be null if the user doesn't want to filter by the date of the last visit.
		 */
		private Category _chosenCategory;

		/*
		 *  Optional
		 *  Should be null if the user doesn't want to filter by total spendings.
		 */
		private Integer _spendingsLimit;

		private String _searchBy;

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

		public void filterByCatagory(Category category) {
			_chosenCategory = category;
			filter(null);
		}
		//	
		//	private void filterBySpendings(int spendingsLimit) {
		//		Log.i(TAG, "filterBySpendings. Limit = " + spendingsLimit);
		//		
		//		_spendingsLimit = spendingsLimit;
		//		filter(null);
		//	}
		//	
		//	private void unfilterByLastVisit() {
		//		_dateOfLastVisit = null;
		//	}

		private boolean doesSetisfyConstraint(Business business, CharSequence constraint) {
			return (constraint == null) || business.getName().toLowerCase().contains(constraint.toString().toLowerCase());
		}
		
		private boolean doesSetisfyCategory(Business business) {
			String categoryName;
			if ((business.getCategory() != null) && (categoryName=business.getCategory().getName()) != null) {
				return (_chosenCategory == null) || _chosenCategory.getName().equals(categoryName);
			}
			return false;
		}
	}
}
