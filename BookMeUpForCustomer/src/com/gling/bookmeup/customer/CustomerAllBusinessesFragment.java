package com.gling.bookmeup.customer;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.Card.OnCardClickListener;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardGridArrayAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ViewSwitcher;

import com.gling.bookmeup.customer.cards.CardThumbnailRoundCorners;
import com.gling.bookmeup.customer.cards.CategoryCard;
import com.gling.bookmeup.main.OnClickListenerFragment;
import com.gling.bookmeup.main.views.BaseGridViewWrapperView.DisplayMode;
import com.gling.bookmeup.main.views.CardGridViewWrapperView;
import com.gling.bookmeup.main.views.CardListViewWrapperView;
import com.gling.bookmeup.sharedlib.parse.Business;
import com.gling.bookmeup.sharedlib.parse.ParseHelper.Category;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

public class CustomerAllBusinessesFragment extends OnClickListenerFragment implements TextWatcher {

    private static final String TAG = "CustomerAllBusinessesFragment";
    
    final private Fragment _thisFragment = this;
    
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
//	private List<Business> _allBusinesses;
	private HashMap<String, Business> _allBusinesses;
	private List<Card> _filteredBusinesses;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        _allBusinesses = new ArrayList<Business>();
        _allBusinesses = new HashMap<String, Business>();
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
        _allBusinessesAdapter.setRowLayoutId(R.layout.customer_business_card_view);
        
        _allCategoriesView = (CardGridViewWrapperView) view.findViewById(R.id.customer_all_businesses_categoryGridView);
        _allCategoriesView.setAdapter(_categoryAdapter);
        _categoryAdapter.setRowLayoutId(R.layout.customer_category_card_view);
        
        _viewSwitcher = (ViewSwitcher)view.findViewById(R.id.customer_all_businesses_viewSwitcher); 
        
        Activity activity = getActivity();
        if (activity instanceof CustomerMainActivity) {
        	CustomerMainActivity customerActivity = (CustomerMainActivity)activity;
        	customerActivity.setAllFragment(null);
        }
        
        inflateListWithAllCategories();
        inflateListWithAllBusinesses();
        
        return view;
    }
    
    @Override
    public void onPause() {
    	InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		_edtSearch.clearFocus();
		imm.hideSoftInputFromWindow(_edtSearch.getWindowToken(), 0);
    	super.onPause();
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
    	Card card = new CategoryCard(activity, category);
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
                	if ((business.getName() != null) && (business.getCategory() != null)) {
                		_allBusinesses.put(business.getObjectId(), business);
                	}
//                	_allBusinesses.add(business);
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
				for (Business business : _allBusinesses.values()) {
					if (doesSetisfyConstraint(business, constraint) &&
							doesSetisfyCategory(business, categoryId)) {
						final Card card = businessToCard(business, getActivity());
						_filteredBusinesses.add(card);
						card.setOnClickListener(new OnCardClickListener() {
							@Override
							public void onClick(Card arg0, View arg1) {
								Activity activity = getActivity();
								if (activity instanceof CustomerMainActivity) {
									CustomerMainActivity customerActivity = (CustomerMainActivity)activity;
									customerActivity.setChosenBusiness(_allBusinesses.get(card.getId()));
									customerActivity.setLastFragment(_thisFragment);
								}
								
								Fragment fragment = new CustomerBookingProfileFragment();
								getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
//								getFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
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
    	card.setTitle(business.getName());
//    	card.setBackgroundResourceId(android.R.color.holo_orange_dark);
    	if (business.getImageFile() != null) {
    		CardThumbnailRoundCorners thumb = new CardThumbnailRoundCorners(activity, business.getImageFile().getUrl());
    		card.addCardThumbnail(thumb);
    	} else {
    		//TODO
    	}
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
