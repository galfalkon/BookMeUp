//package com.gling.bookmeup.customer;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Locale;
//
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.os.Bundle;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.ArrayAdapter;
//import android.widget.EditText;
//import android.widget.Filter;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import com.gling.bookmeup.R;
//import com.gling.bookmeup.business.Business;
//import com.gling.bookmeup.main.OnClickListenerFragment;
//import com.gling.bookmeup.main.ParseHelper.Category;
//import com.parse.FindCallback;
//import com.parse.ParseException;
//import com.parse.ParseObject;
//import com.parse.ParseQuery;
//import com.parse.ParseQueryAdapter;
//
//public class CustomerAllBusinessesFragment extends OnClickListenerFragment implements /*OnClickListener,*/ TextWatcher {
//
//	private static final String TAG = "CustomerMainActivity";
//
//	private List<Business> _allBusinesses, _filteredBusinesses;
//	private BusinessesArrayAdapter _businessesListViewAdapter;
//	private ParseQueryAdapter<Category> _categoriesAdapter;
////	private ArrayAdapter<String> _categoriesAdapter;
//	ListView servicesListView = null;
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		Log.i(TAG, "onCreate");
//		super.onCreate(savedInstanceState);
//		
////		_customer = ((CustomerMainActivity)getActivity()).getCustomer();
//		
//		_allBusinesses = new ArrayList<Business>();
//		_filteredBusinesses = new ArrayList<Business>();
//		_businessesListViewAdapter = new BusinessesArrayAdapter();
//
//
//		ParseQuery<Business> getBusinessQuery = new ParseQuery<Business>(Business.CLASS_NAME);
//		getBusinessQuery.include(Business.Keys.CATEGORY);
//
//		final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...");
//		getBusinessQuery.findInBackground(new FindCallback<Business>() {
//			@Override
//			public void done(List<Business> objects, ParseException e) {
//				progressDialog.dismiss();
//				if (e != null) {
//					Log.e(TAG, "Exception: " + e.getMessage());
//					return;
//				}
//
//				for (Business business : objects) {
//					int index = _allBusinesses.indexOf(business);
//					if (index == -1) {
//						_allBusinesses.add(business);
//					} else {
//					}
//				}
//
//
//				_businessesListViewAdapter.notifyDataSetChanged();
//			}
//		});
//		
//		_categoriesAdapter = new ParseQueryAdapter<Category>(getActivity(),
//				Category.CLASS_NAME);
//		_categoriesAdapter.setTextKey(Category.Keys.NAME);
//		
////		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
////    	Set<String> categorySet = new HashSet<String>();
////    	categorySet = sp.getStringSet(ParseHelper.BUSINESS_CATEGORIES, categorySet);
////    	String[] categoryArr = categorySet.toArray(new String[categorySet.size()]);
////    	_categoriesAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_single_choice, categoryArr);
//	}
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		Log.i(TAG, "onCreateView");
//		
//		View view = super.onCreateView(inflater, container, savedInstanceState);
//		
//		Log.i(TAG, "list view is initialized");
//		servicesListView = (ListView)view.findViewById(R.id.customer_business_services_dialog_listview);
//
//		final ListView businessesListView = (ListView)view.findViewById(R.id.customer_business_list_listViewBusinesses);
//		businessesListView.setAdapter(_businessesListViewAdapter);
//		businessesListView.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                    int position, long id) {
//				Business business = (Business) businessesListView.getItemAtPosition(position);
//				Log.i(TAG, "business: " + business.getName());
//				CustomerChooseBusinessDialogs dialog = new CustomerChooseBusinessDialogs();
//				dialog.createBusinessProfileDialog(business, getActivity(), getResources(), Customer.getCurrentCustomer());
//            }
//
//			
//        });
//
//		EditText edtSearch = (EditText)view.findViewById(R.id.customer_business_list_edtSearch);
//		edtSearch.addTextChangedListener(this);
//
//		final ListView categoriesListView = (ListView)view.findViewById(R.id.customer_business_list_listViewBusinessesCategories);
//		categoriesListView.setAdapter(_categoriesAdapter);
//		categoriesListView.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                    int position, long id) {
//				ParseObject object = (ParseObject) categoriesListView.getItemAtPosition(position);
//				Category category = (Category) object;
////				String category = _categoriesAdapter.getItem(position);
//				_businessesListViewAdapter.getBusinessFilter().filterByCatagory(category);
//            }
//
//			
//        });
//		
//		return view;
//	}
//	
//	@Override
//	protected int getFragmentLayoutId() {
//		return R.layout.customer_all_businesses_fragment;
//	}
//
//	@Override
//	public void onClick(View v) {
//	}
//
//	@Override
//	public void afterTextChanged(Editable s) {
//		Log.i(TAG, "afterTextChanged");
//	}
//
//	@Override
//	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//		Log.i(TAG, "beforeTextChanged");		
//	}
//
//	@Override
//	public void onTextChanged(CharSequence s, int start, int before	, int count) {
//		Log.i(TAG, "onTextChanged");
//		_businessesListViewAdapter.getFilter().filter(s);
//		//		}
//	}
//
//
//	private class BusinessesArrayAdapter extends ArrayAdapter<Business> {
//
//		private BusinessFilter _businessFilter;
//
//		public BusinessesArrayAdapter() {
//			super(getActivity(), R.layout.customer_business_list_item, _filteredBusinesses);
//			_businessFilter = new BusinessFilter();
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
//			if (business.getCategory() != null) {
//				totalSepndingsTextView.setText(business.getCategory().getName());
////				totalSepndingsTextView.setText(business.getCategory());
//			} else {
//				totalSepndingsTextView.setText("No Category");
//			}
//
//			return convertView;
//		}
//
//		@Override
//		public Filter getFilter() {
//			Log.i(TAG, "getFilter");
//			return _businessFilter;
//		}
//		
//		public BusinessFilter getBusinessFilter() {
//			Log.i(TAG, "getBusinessFilter");
//			return _businessFilter;
//		}
//	}
//
//	private class BusinessFilter extends Filter {
//		private Category _chosenCategory;
////		private String _chosenCategory;
//
//		@Override
//		protected FilterResults performFiltering(CharSequence constraint) {
//			Log.i(TAG, "performFiltering(" + constraint + ")");
//
//			FilterResults results = new FilterResults();
//
//			_filteredBusinesses.clear();
//			if (constraint != null && !constraint.equals("")) {
//				for (Business business : _allBusinesses) {
//					if (doesSetisfyConstraint(business, constraint)) {
//						_filteredBusinesses.add(business);
//					}
//				}
//			} else if(_chosenCategory != null) {
//				for (Business business : _allBusinesses) {
//					if (doesSetisfyCategory(business)) {
//						_filteredBusinesses.add(business);
//					}
//				}
//			}
//
//			results.values = _filteredBusinesses;
//			results.count = _filteredBusinesses.size();
//
//			return results;
//		}
//
//		@Override
//		protected void publishResults(CharSequence constraint, FilterResults results) {
//			Log.i(TAG, "publicResults");
//			_businessesListViewAdapter.notifyDataSetChanged();
//		}
//
//		public void filterByCatagory(Category category) {
////		public void filterByCatagory(String category) {
//			_chosenCategory = category;
//			filter(null);
//		}
//
//		private boolean doesSetisfyConstraint(Business business, CharSequence constraint) {
//			return (constraint == null) || business.getName().toLowerCase(Locale.getDefault()).contains(constraint.toString().toLowerCase(Locale.getDefault()));
//		}
//		
//		private boolean doesSetisfyCategory(Business business) {
//			String categoryName;
//			if ((business.getCategory() != null) && (categoryName=business.getCategory().getName()) != null) {
//				return (_chosenCategory == null) || _chosenCategory.getName().equals(categoryName);
////			if ((categoryName = business.getCategory().getName()) != null) {
////				return (_chosenCategory == null) || _chosenCategory.equals(categoryName);
//			}
//			return false;
//		}
//	}
//	
//}

//package com.gling.bookmeup.customer;
//
//import it.gmariotti.cardslib.library.internal.Card;
//import it.gmariotti.cardslib.library.internal.Card.OnCardClickListener;
//import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
//import it.gmariotti.cardslib.library.internal.CardGridArrayAdapter;
//import it.gmariotti.cardslib.library.internal.CardHeader;
//import it.gmariotti.cardslib.library.internal.CardThumbnail;
//import it.gmariotti.cardslib.library.view.CardGridView;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ViewSwitcher;
//
//import com.gling.bookmeup.R;
//import com.gling.bookmeup.business.Business;
//import com.gling.bookmeup.main.GenericCardArrayAdapter;
//import com.gling.bookmeup.main.ICardGenerator;
//import com.gling.bookmeup.main.IObservableList;
//import com.gling.bookmeup.main.ObservableArrayList;
//import com.gling.bookmeup.main.OnClickListenerFragment;
//import com.gling.bookmeup.main.views.BaseListViewWrapperView.DisplayMode;
//import com.gling.bookmeup.main.views.CardListViewWrapperView;
//import com.parse.FindCallback;
//import com.parse.ParseException;
//import com.parse.ParseQuery;
//
//public class CustomerAllBusinessesFragment extends OnClickListenerFragment {
//
//    private static final String TAG = "CustomerAllBusinessesFragment";
//    
//    private IObservableList<Business> _allBusinesses, _businessesByType;
//    private GenericCardArrayAdapter<Business> _allBusinessesAdapter, _businessesByTypeAdapter;
////    private exp_businessesByTypeAdapter;
//
//	private ViewSwitcher _viewSwitcher;
//	
//	private CardListViewWrapperView _allBusinessesListView, _businessesByTypeListView;
////	private CardExpandableListViewWrapperView _businessesByTypeListView;
//	
//	private CardGridView _gefenView;
//	private CardGridArrayAdapter _gefenAdapter;
//	
//	static final String[] items = new String[]{"Feed", "Nap", "Diaper", "Milestone", "Diary"};
//    static final Integer[] imageId = {  R.drawable.logo_with_title,
//                                        R.drawable.logo_with_title,
//                                        R.drawable.logo_with_title,
//                                        R.drawable.logo_with_title,
//                                        R.drawable.logo_with_title};
//	
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        /**
//        _allBusinesses = new ObservableArrayList<Business>();
//        _businessesByType = new ObservableArrayList<Business>();
//        _allBusinessesAdapter = new GenericCardArrayAdapter<Business>(getActivity(), _allBusinesses, new AllBusinessesCardGenerator());
//        _businessesByTypeAdapter = new GenericCardArrayAdapter<Business>(getActivity(), _businessesByType, new BusinessesByTypeCardGenerator());
//        */
////        CardHeader aHeader = new CardHeader(getActivity());
////        aHeader.setTitle("a");
////        Card a = new Card(getActivity());
////        a.addCardHeader(aHeader);
////        a.setTitle("a");
////        CardHeader bHeader = new CardHeader(getActivity());
////        aHeader.setTitle("b");
////        Card b = new Card(getActivity());
////        b.addCardHeader(bHeader);
////        b.setTitle("b");
////        ArrayList<Card> cards = new ArrayList<Card>();
////        cards.add(a);
////        cards.add(b);
////        _gefenAdapter = new CardGridArrayAdapter(getActivity(), cards); 
//    }
//    
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//            Bundle savedInstanceState) {
//        View view = super.onCreateView(inflater, container, savedInstanceState);
//        /**
//        _allBusinessesListView = (CardListViewWrapperView)view.findViewById(R.id.customer_all_businesses_cardListViewAllBusinesses);
//        _allBusinessesListView.setAdapter(_allBusinessesAdapter);
//        
////        _businessesByTypeListView = (CardExpandableListViewWrapperView)view.findViewById(R.id.customer_all_businesses_cardListViewBusinessesByType);
//        _businessesByTypeListView = (CardListViewWrapperView)view.findViewById(R.id.customer_all_businesses_cardListViewBusinessesByType);
//        _businessesByTypeListView.setAdapter(_businessesByTypeAdapter);
//        
//        _viewSwitcher = (ViewSwitcher)view.findViewById(R.id.customer_all_businesses_viewSwitcher); 
//        
//        inflateListWithAllBusinesses();
//        **/
////        _gefenView = (GefenGridView) view.findViewById(R.id.gefenView);
////        _gefenAdapter.notifyDataSetChanged();
//        
//        _gefenView = (CardGridView) view.findViewById(R.id.cardGridMenu);
//        //create Card list
//        ArrayList<Card> cards = new ArrayList<Card>();
//        for(int i = 0; i < items.length; i++){
//            Card c = new Card(getActivity());
//            //add header
//            CardHeader cheader = new CardHeader(getActivity());
//            cheader.setTitle(items[i]);
//            c.addCardHeader(cheader);
//            //add card thumb nail
//            CardThumbnail thumb = new CardThumbnail(getActivity());
//            thumb.setDrawableResource(imageId[i]);
//            c.addCardThumbnail(thumb);
//
//            cards.add(c);
//        }
//        CardArrayAdapter cardAdapter = new CardArrayAdapter(getActivity(), cards);
//        _gefenView.setAdapter(cardAdapter);
//        
//        return view;
//    }
//
//    @Override
//    public void onClick(View v) {
//    	/**
//    	switch (v.getId())
//    	{
//    	case R.id.customer_all_businesses_btnAll:
//    		Log.i(TAG, "btnPending clicked");
//    		showAllBusinesses();
//    		break;
//    	case R.id.customer_all_businesses_btnByType:
//    		Log.i(TAG, "btnApproved clicked");
//    		showBusinessesByType();
//    		break;
//    	}
//    	*/
//    }
//
//    @Override
//    protected int getFragmentLayoutId() {
//        return R.layout.customer_all_businesses_fragment;
//    }
//
//    private void inflateListWithAllBusinesses() {
//    	ParseQuery<Business> query = new ParseQuery<Business>(Business.CLASS_NAME);
//    	query.include(Business.Keys.CATEGORY);
//		
//        _allBusinessesListView.setDisplayMode(DisplayMode.LOADING_VIEW);
//        _businessesByTypeListView.setDisplayMode(DisplayMode.LOADING_VIEW);
//        query.findInBackground(new FindCallback<Business>() {
//            @Override
//            public void done(List<Business> objects, ParseException e) {
//                Log.i(TAG, "Done querying businesses. #objects = " + objects.size());
//                if (e != null) {
//                    Log.e(TAG, "Exception occurred: " + e.getMessage());
//                    return;
//                }
//                
//                for (Business business : objects)
//                {
//                	_allBusinesses.add(business);
//                }
//                
//                updateAllBusinessesDisplayMode();
//                updateBusinessesByTypeDisplayMode();
//                _allBusinessesAdapter.notifyDataSetChanged();
//                _businessesByTypeAdapter.notifyDataSetChanged();
//            }
//        });
//    }
//    
//    private void showAllBusinesses()
//    {
//    	if (_viewSwitcher.getDisplayedChild() != 0)
//    	{
//    		_viewSwitcher.setDisplayedChild(0);
//    	}
//    }
//    
//    private void showBusinessesByType()
//    {
//    	if (_viewSwitcher.getDisplayedChild() != 1)
//    	{
//    		_viewSwitcher.setDisplayedChild(1);
//    	}
//    }
//    
//    private void updateAllBusinessesDisplayMode()
//    {
//    	// Update display mode
//    	DisplayMode newDisplayMode = _allBusinesses.isEmpty()? DisplayMode.NO_ITEMS_VIEW : DisplayMode.LIST_VIEW;
//    	_allBusinessesListView.setDisplayMode(newDisplayMode);
//    }
//    
//    private void updateBusinessesByTypeDisplayMode()
//    {
//    	// Update display mode
//    	DisplayMode newDisplayMode = _businessesByType.isEmpty()? DisplayMode.NO_ITEMS_VIEW : DisplayMode.LIST_VIEW;
//    	_businessesByTypeListView.setDisplayMode(newDisplayMode);
//    }
//    
//    private class AllBusinessesCardGenerator implements ICardGenerator<Business>
//    {
//		@Override
//		public Card generateCard(final Business business) 
//		{
//			CardHeader cardHeader = new CardHeader(getActivity());
//	    	cardHeader.setTitle(business.getName());
//	    	
//	    	Card card = new Card(getActivity());
//	    	card.addCardHeader(cardHeader);
////	    	card.setTitle(
////	    			"Service: " + business.getServiceName() + "\n" +
////	    			"Date: " + Constants.DATE_FORMAT.format(business.getDate()));
//
//	    	card.setClickable(true);
//	    	card.setOnClickListener(new OnCardClickListener() 
//	    	{
//				@Override
//				public void onClick(Card card, View view) 
//				{
////					Log.i(TAG, "Pending booking clicked");
////					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
////		            builder.setMessage(R.string.business_bookings_list_pending_click_dialog)
////		            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
////		                public void onClick(DialogInterface dialog, int id) {
////		                    Log.i(TAG, "Approving pending booking");
////		                    business.setStatus(Status.APPROVED);
////		                    business.saveInBackground();
////		                    
////		                    _pendingBookings.remove(business);
////		                    _approvedBookings.add(business);
////		                    updatePendingBookingsTitleAndDisplayMode();
////		                    updateApprovedBookingsTitleAndDisplayMode();
////		                    
////		                    PushUtils.notifyCustomerAboutApprovedBooking(business, new SendCallback() {
////								@Override
////								public void done(ParseException e) {
////									Log.i(TAG, "notifyCustomerAboutApprovedBooking done");
////									if (e != null)
////									{
////										Log.e(TAG, "Exception: " + e.getMessage());
////										return;
////									}
////								}
////							});
////		                }
////		            })
////		            .setNegativeButton(R.string.cancel, null);
////			        builder.show();
//				}
//	    	});
//	    	
//	    	return card;
//		}
//    }
//    
//    private class BusinessesByTypeCardGenerator implements ICardGenerator<Business>
//    {
//		@Override
//		public Card generateCard(final Business business) 
//		{
//			CardHeader cardHeader = new CardHeader(getActivity());
//	    	cardHeader.setTitle(business.getName());
//	    	
//	    	Card card = new Card(getActivity());
//	    	card.addCardHeader(cardHeader);
////	    	card.setTitle(
////	    			"Service: " + business.getServiceName() + "\n" +
////	    			"Date: " + Constants.DATE_FORMAT.format(business.getDate()));
//
//	    	card.setClickable(true);
//	    	card.setOnClickListener(new OnCardClickListener() {
//				
//				@Override
//				public void onClick(Card card, View view) {
////					Log.i(TAG, "Approved booking clicked");
////					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
////		            builder.setMessage(R.string.business_bookings_list_approved_click_dialog)
////		            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
////		                public void onClick(DialogInterface dialog, int id) {
////		                    Log.i(TAG, "Deleting approved booking");
////		                    business.setStatus(Status.CANCELED);
////		                    business.saveInBackground();
////		                    
////		                    _approvedBookings.remove(business);
////		                    updateApprovedBookingsTitleAndDisplayMode();
////		                    
////		                    PushUtils.notifyCustomerAboutCanceledBooking(business, new SendCallback() {
////								@Override
////								public void done(ParseException e) {
////									Log.i(TAG, "notifyCustomerAboutCanceledBooking done");
////									if (e != null)
////									{
////										Log.e(TAG, "Exception: " + e.getMessage());
////										return;
////									}
////								}
////							});
////		                }
////		            })
////		            .setNegativeButton(R.string.cancel, null);
////		            builder.show();
//				}
//			});
//	    	
//	    	return card;
//		}
//    }
//}

package com.gling.bookmeup.customer;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.Card.OnCardClickListener;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardGridArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ViewSwitcher;

import com.gling.bookmeup.R;
import com.gling.bookmeup.business.Business;
import com.gling.bookmeup.business.BusinessCalendarActivity;
import com.gling.bookmeup.main.OnClickListenerFragment;
import com.gling.bookmeup.main.ParseHelper.Category;
import com.gling.bookmeup.main.views.BaseGridViewWrapperView.DisplayMode;
import com.gling.bookmeup.main.views.CardGridViewWrapperView;
import com.gling.bookmeup.main.views.CardListViewWrapperView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

public class CustomerAllBusinessesFragment extends OnClickListenerFragment implements TextWatcher {

    private static final String TAG = "CustomerAllBusinessesFragment";
    
    EditText _edtSearch;
    
    //category view
	private CardGridViewWrapperView _allCategoriesView;
	private CardGridArrayAdapter _categoryAdapter;
	private ArrayList<Card> _allCategoriesCards;
//	private HashMap<String, Category> _allParseCategories;
	
	//switcher
	private ViewSwitcher _viewSwitcher;
	
	//businesses view
	private CardListViewWrapperView _allBusinessesListView;
	private BusinessCardArrayAdapter _allBusinessesAdapter;
//	private GenericCardArrayAdapter<Business> _allBusinessesAdapter;
	private List<Business> _allBusinesses;
//	private List<Business> _allBusinesses;
	private List<Card> _filteredBusinesses;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _allBusinesses = new ArrayList<Business>();
        _filteredBusinesses = new ArrayList<Card>();
//        _allBusinessesAdapter = new GenericCardArrayAdapter<Business>(getActivity(), _allBusinesses, new AllBusinessesCardGenerator());
        _allBusinessesAdapter = new BusinessCardArrayAdapter(getActivity(), _filteredBusinesses);
        _allCategoriesCards = new ArrayList<Card>();
//        _allParseCategories = new HashMap<String, Category>();
        _categoryAdapter = new CardGridArrayAdapter(getActivity(), _allCategoriesCards);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        
        _edtSearch = (EditText)view.findViewById(R.id.customer_all_businesses_list_edtSearch);
		_edtSearch.addTextChangedListener(this);
        
        _allBusinessesListView = (CardListViewWrapperView)view.findViewById(R.id.customer_all_businesses_cardListViewAllBusinessesByType);
        _allBusinessesListView.setAdapter(_allBusinessesAdapter);
        
        _allCategoriesView = (CardGridViewWrapperView) view.findViewById(R.id.customer_all_businesses_categoryGridView);
        _allCategoriesView.setAdapter(_categoryAdapter);
        
        _viewSwitcher = (ViewSwitcher)view.findViewById(R.id.customer_all_businesses_viewSwitcher); 
        
        inflateListWithAllCategories();
        inflateListWithAllBusinesses();
        
        return view;
    }

    @Override
    public void onClick(View v) {
    	/**
    	switch (v.getId())
    	{
    	case R.id.customer_all_businesses_btnAll:
    		Log.i(TAG, "btnPending clicked");
    		showAllBusinesses();
    		break;
    	case R.id.customer_all_businesses_btnByType:
    		Log.i(TAG, "btnApproved clicked");
    		showBusinessesByType();
    		break;
    	}
    	*/
    }

    @Override
    protected int getFragmentLayoutId() {
        return R.layout.customer_all_businesses_fragment;
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
		String categoryFilter = _allBusinessesAdapter._businessFilter.getCategoryFilter();
		if ((categoryFilter == null) && ((s == null) || ("".equals(s.toString())))) {
			showCategoriesView();
		} else {
			showBusinessesByTypeView(s);
		}
	}

    private void inflateListWithAllCategories() {
    	ParseQuery<Category> query = new ParseQuery<Category>(Category.CLASS_NAME);
    	_allCategoriesView.setDisplayMode(DisplayMode.LOADING_VIEW);
    	
        query.findInBackground(new FindCallback<Category>() {
            @Override
            public void done(List<Category> objects, ParseException e) {
                Log.i(TAG, "Done querying businesses. #objects = " + objects.size());
                if (e != null) {
                    Log.e(TAG, "Exception occurred: " + e.getMessage());
                    return;
                }
                
                for (Category category: objects)
                {
                	Card card = categoryToCard(category, getActivity());
        			card.setOnClickListener(new OnCardClickListener() {
        				
        				@Override
        				public void onClick(Card card, View view) {
        					showBusinessesByTypeView(card.getId(), null);
        					
        				}
        			});
                	_allCategoriesCards.add(card);
                }
                
                _categoryAdapter.notifyDataSetChanged();
                updateCategoriesDisplayMode();
            }
        });
    }
    
    private static Card categoryToCard(Category category, Activity activity) {
    	Card card = new Card(activity);
    	CardHeader header = new CardHeader(activity);
    	header.setTitle(category.getName());
    	card.addCardHeader(header);
//    	card.setCardView(cardView)
//    	card.
//    	CardThumbnail thumb = new CardThumbnail(activity);
//    	thumb.setDrawableResource(R.drawable.logo_with_title);
//    	card.addCardThumbnail(thumb);
    	card.setId(category.getObjectId());
    	return card;
    }
 
    private void inflateListWithAllBusinesses() {
    	ParseQuery<Business> query = new ParseQuery<Business>(Business.CLASS_NAME);
    	query.include(Business.Keys.CATEGORY);
    	
    	_allBusinessesListView.setDisplayMode(com.gling.bookmeup.main.views.BaseListViewWrapperView.DisplayMode.LOADING_VIEW);
        query.findInBackground(new FindCallback<Business>() {
            @Override
            public void done(List<Business> objects, ParseException e) {
                Log.i(TAG, "Done querying businesses. #objects = " + objects.size());
                if (e != null) {
                    Log.e(TAG, "Exception occurred: " + e.getMessage());
                    return;
                }
                
                for (Business business: objects)
                {
                	_allBusinesses.add(business);
                }
                updateBusinessesDisplayMode();
            }
        });
    }
    
    public void clearAll() {
    	Log.i(TAG, "clearAll");
    	BusinessFilter filter = _allBusinessesAdapter._businessFilter;
    	filter.setCategoryFilter(null);
    	_edtSearch.setText(null);
    }
    
    private void showCategoriesView()
    {
    	Log.i(TAG, "showCategoriesView");
//    	_edtSearch.setText(null);
    	Activity activity = getActivity();
    	if (activity instanceof CustomerMainActivity) {
    		((CustomerMainActivity)activity).setAllFragment(null);
    	}
    	if (_viewSwitcher.getDisplayedChild() != 0)
    	{
    		_viewSwitcher.setDisplayedChild(0);
    	}
//    	BusinessFilter filter = _allBusinessesAdapter._businessFilter;
//    	filter.addCategoryFilter(null);
    }
  
    private void showBusinessesByTypeView(CharSequence s)
    {
    	Log.i(TAG, "showBusinessByTypeView");
    	Activity activity = getActivity();
    	if (activity instanceof CustomerMainActivity) {
    		((CustomerMainActivity)activity).setAllFragment(this);
    	}
    	if (_viewSwitcher.getDisplayedChild() != 1)
    	{
    		_viewSwitcher.setDisplayedChild(1);
    	}
    	BusinessFilter filter = _allBusinessesAdapter._businessFilter;
    	filter.filter(s);
    }
    
    private void showBusinessesByTypeView(String categoryId, CharSequence s)
    {
    	Log.i(TAG, "showBusinessByTypeView");
    	Activity activity = getActivity();
    	if (activity instanceof CustomerMainActivity) {
    		((CustomerMainActivity)activity).setAllFragment(this);
    	}
    	if (_viewSwitcher.getDisplayedChild() != 1)
    	{
    		_viewSwitcher.setDisplayedChild(1);
    	}
    	BusinessFilter filter = _allBusinessesAdapter._businessFilter;
    	filter.setCategoryFilter(categoryId);
    	filter.filter(s);
    }
    
    private void updateCategoriesDisplayMode()
    {
    	// Update display mode
    	DisplayMode newDisplayMode = _allCategoriesCards.isEmpty()? DisplayMode.NO_ITEMS_VIEW : DisplayMode.LIST_VIEW;
    	_allCategoriesView.setDisplayMode(newDisplayMode);
    }
    
    private void updateBusinessesDisplayMode()
    {
    	// Update display mode
    	com.gling.bookmeup.main.views.BaseListViewWrapperView.DisplayMode newDisplayMode = 
    			_filteredBusinesses.isEmpty()? 
    					com.gling.bookmeup.main.views.BaseListViewWrapperView.DisplayMode.NO_ITEMS_VIEW : 
    						com.gling.bookmeup.main.views.BaseListViewWrapperView.DisplayMode.LIST_VIEW;
    	_allBusinessesListView.setDisplayMode(newDisplayMode);
    }
    
    private class BusinessFilter extends Filter {
    	
    	String categoryId = null;
    	
    	public void setCategoryFilter(String categoryId) {
    		this.categoryId = categoryId;
    	}
    	
    	public String getCategoryFilter() {
    		return this.categoryId;
    	}

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			Log.i(TAG, "performFiltering(" + constraint + ")");

			FilterResults results = new FilterResults();

			_filteredBusinesses.clear();
			if ((constraint == null) && (categoryId == null)) {
				showCategoriesView();
			} else {
				for (Business business : _allBusinesses) {
					if (doesSetisfyConstraint(business, constraint) &&
							doesSetisfyCategory(business, categoryId)) {
						final Card card = businessToCard(business, getActivity());
						_filteredBusinesses.add(card);
						card.setOnClickListener(new OnCardClickListener() {
							@Override
							public void onClick(Card arg0, View arg1) {
								//TODO: card on click
								Intent intent = new Intent(getActivity(), CustomerCalendarActivity.class);
								intent.putExtra(CustomerCalendarActivity.BUSINESS_ID_EXTRA, card.getId());
								startActivity(intent);
							}
						});
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
			_allBusinessesAdapter.notifyDataSetChanged();
		}

		private boolean doesSetisfyConstraint(Business business, CharSequence constraint) {
			return (constraint == null) || business.getName().toLowerCase().contains(constraint.toString().toLowerCase());
		}
		
		private boolean doesSetisfyCategory(Business business, String categoryId) {
			return (categoryId == null) || business.getCategory().getObjectId().toLowerCase().equals(categoryId.toLowerCase());
		}
	}
    
    private static Card businessToCard(Business business, Activity activity) {
    	Card card = new Card(activity);
    	CardHeader header = new CardHeader(activity);
    	header.setTitle(business.getName());
    	card.addCardHeader(header);
//    	card.setCardView(cardView)
//    	card.
//    	CardThumbnail thumb = new CardThumbnail(activity);
//    	thumb.setDrawableResource(R.drawable.logo_with_title);
//    	card.addCardThumbnail(thumb);
    	card.setId(business.getObjectId());
    	return card;
    }
    
    private class BusinessCardArrayAdapter extends CardArrayAdapter {//CardArrayMultiChoiceAdapter {

		public BusinessFilter _businessFilter;

		public BusinessCardArrayAdapter(Context context, List<Card> cards) {
			super(context, cards);

			_businessFilter = new BusinessFilter();
		}

		@Override
		public Filter getFilter() {
			Log.i(TAG, "getFilter");
			return _businessFilter;
		}

		@Override
			public void notifyDataSetChanged() {
				super.notifyDataSetChanged();
				updateBusinessesDisplayMode();
			}
	}
}
