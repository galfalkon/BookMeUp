package com.gling.bookmeup.customer;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.Card.OnCardClickListener;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
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
import android.widget.ListView;

import com.gling.bookmeup.customer.cards.BusinessCard;
import com.gling.bookmeup.main.OnClickListenerFragment;
import com.gling.bookmeup.main.views.BaseListViewWrapperView.DisplayMode;
import com.gling.bookmeup.main.views.CardListViewWrapperView;
import com.gling.bookmeup.sharedlib.parse.Customer;
import com.parse.ParseException;
import com.parse.ParseObject;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class CustomerFavouriteFragment extends OnClickListenerFragment implements TextWatcher {
	private static final String TAG = "CustomerMainActivity";
	
	final private Fragment _thisFragment = this;
	
	EditText _edtSearch;

	private HashMap<String, Business> _allBusinesses;
	private HashMap<String, com.gling.bookmeup.sharedlib.parse.Business> _allParseBusinesses;
	private BusinessCardArrayAdapter _businessesCardAdapter;
	private List<Card> _filteredBusinesses;

	private CardListViewWrapperView _businessesCardListView; 

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView");

		_allBusinesses = new HashMap<String, Business>();
		_allParseBusinesses = new HashMap<String, com.gling.bookmeup.sharedlib.parse.Business>();
		_filteredBusinesses = new ArrayList<Card>();
		_businessesCardAdapter = new BusinessCardArrayAdapter(getActivity(), _filteredBusinesses);

		View view = super.onCreateView(inflater, container, savedInstanceState);
		_businessesCardListView = (CardListViewWrapperView) view.findViewById(R.id.customer_favourites_business_list_listViewBusinesses);
		_businessesCardListView.setAdapter(_businessesCardAdapter);
		_businessesCardListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		// should be called AFTER _businessesCardListView.setAdapter()
        _businessesCardAdapter.setRowLayoutId(R.layout.customer_business_card_view);

		_edtSearch = (EditText)view.findViewById(R.id.customer_favourites_business_list_edtSearch);
		_edtSearch.addTextChangedListener(this);

		new PopulateFavouritesBusinessesTask().execute();
		return view;
	}
	
    @Override
    public void onPause() {
    	InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(_edtSearch.getWindowToken(), 0);
    	super.onPause();
    }

	@Override
	protected int getFragmentLayoutId() {
		return R.layout.customer_favourites_fragment;
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
		_businessesCardAdapter.getFilter().filter(s);
	}
	
	private class PopulateFavouritesBusinessesTask extends AsyncTask<Void, Void, Void>
	{
		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();
			_businessesCardListView.setDisplayMode(DisplayMode.LOADING_VIEW);
		}
		
		@Override
		protected Void doInBackground(Void... params) 
		{
		    if (Customer.getCurrentCustomer() == null) {
		        return null;
		    }
		    
			@SuppressWarnings("unchecked")
			ArrayList<ParseObject> favouriteBusinesses = (ArrayList<ParseObject>) Customer.getCurrentCustomer().get(Customer.Keys.FAVOURITES);
			
			if (favouriteBusinesses == null) 
			{
				return null;
			}
			
			for (ParseObject parseObject : favouriteBusinesses) {
				if (parseObject instanceof com.gling.bookmeup.sharedlib.parse.Business) 
				{
					com.gling.bookmeup.sharedlib.parse.Business businessItem = (com.gling.bookmeup.sharedlib.parse.Business) parseObject;
					try 
					{
						ParseObject businessObject = businessItem.fetchIfNeeded();
						businessItem = (com.gling.bookmeup.sharedlib.parse.Business) businessObject;

						Business currentBusiness = new Business(businessItem);
						Card businessCard = currentBusiness.toCard(getActivity());
						if (!_allBusinesses.containsKey(currentBusiness._id)) 
						{						
							_filteredBusinesses.add(businessCard);
							_allBusinesses.put(currentBusiness._id, currentBusiness);
						}

						String id = businessItem.getObjectId();
						if (!_allParseBusinesses.containsKey(id)) 
						{
							_allParseBusinesses.put(id, businessItem);
						}
					} 
					catch (ParseException e) 
					{
						Log.e(TAG, "Exception: " + e.getMessage());
					}
				}
				
				publishProgress();
			}
			
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Void... values) 
		{
			super.onProgressUpdate(values);
			if (!_allBusinesses.isEmpty())
			{
				_businessesCardListView.setDisplayMode(DisplayMode.LIST_VIEW);
			}
			
			_businessesCardAdapter.notifyDataSetChanged();
		}
		
		protected void onPostExecute(Void result) 
		{
			DisplayMode newDisplayMode = _allBusinesses.isEmpty()? DisplayMode.NO_ITEMS_VIEW : DisplayMode.LIST_VIEW;
			_businessesCardListView.setDisplayMode(newDisplayMode);
		};
	}

	private class Business {
		public final String _id, _businessName;
		private com.gling.bookmeup.sharedlib.parse.Business _business;

		public Business(com.gling.bookmeup.sharedlib.parse.Business business) {
			_id = business.getObjectId();
			_businessName = business.getName();
			_business = business;
		}

		@Override
		public String toString() {
			return _businessName;
		}

		@Override
		public boolean equals(Object other) {
			return !(other instanceof Business) || (_id.equals(((Business)other)._id)); 
		}

		public Card toCard(Context context) {
		    Card card = new BusinessCard(context, _business);
            card.setId(_id);
            card.setOnClickListener(new OnCardClickListener() 
			{

				@Override
				public void onClick(Card businessCard, View arg1) 
				{
					if (!_allParseBusinesses.containsKey(businessCard.getId())) 
					{
						Log.i(TAG, "Business Dialog - could not find business");
						Crouton.showText(getActivity(), "Could not find business", Style.ALERT);
						return;
					}
					com.gling.bookmeup.sharedlib.parse.Business business = _allParseBusinesses.get(businessCard.getId());
//					Log.i(TAG, "Business Dialog - " + business.getName());
//					CustomerChooseBusinessDialogs dialog = new CustomerChooseBusinessDialogs();
//					dialog.createBusinessProfileDialog(business, getActivity(), getResources(), Customer.getCurrentCustomer());
					
					Activity activity = getActivity();
					if (activity instanceof CustomerMainActivity) {
						CustomerMainActivity customerActivity = (CustomerMainActivity)activity;
						customerActivity.setChosenBusiness(business);
						customerActivity.setLastFragment(_thisFragment);
//						customerActivity.setChosenBusiness(_allBusinesses.get(card.getId()));
					}
//					
					Fragment fragment = new CustomerBookingProfileFragment();
					getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
				}
			});
            
            
            return card;
		}
	}

	private class BusinessFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			Log.i(TAG, "performFiltering(" + constraint + ")");

			FilterResults results = new FilterResults();

			_filteredBusinesses.clear();
			for (Business business : _allBusinesses.values()) {
				if (doesSetisfyConstraint(business, constraint)) {
					_filteredBusinesses.add(business.toCard(getActivity()));
				}
			}

			results.values = _filteredBusinesses;
			results.count = _filteredBusinesses.size();

			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			Log.i(TAG, "publicResults");
			_businessesCardAdapter.notifyDataSetChanged();
		}

		private boolean doesSetisfyConstraint(Business business, CharSequence constraint) {
			return (constraint == null) || business._businessName.toLowerCase().contains(constraint.toString().toLowerCase());
		}
	}

	private class BusinessCardArrayAdapter extends CardArrayAdapter {//CardArrayMultiChoiceAdapter {

		private BusinessFilter _businessFilter;

		public BusinessCardArrayAdapter(Context context, List<Card> cards) {
			super(context, cards);

			_businessFilter = new BusinessFilter();
		}

		@Override
		public Filter getFilter() {
			Log.i(TAG, "getFilter");
			return _businessFilter;
		}

	}
}

