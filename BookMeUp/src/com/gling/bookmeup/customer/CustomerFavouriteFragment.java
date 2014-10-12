package com.gling.bookmeup.customer;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.Card.OnCardClickListener;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;

import com.gling.bookmeup.R;
import com.gling.bookmeup.main.OnClickListenerFragment;
import com.gling.bookmeup.main.views.BaseListViewWrapperView.DisplayMode;
import com.gling.bookmeup.main.views.CardListViewWrapperView;
import com.parse.ParseException;
import com.parse.ParseObject;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class CustomerFavouriteFragment extends OnClickListenerFragment implements TextWatcher {
	private static final String TAG = "CustomerMainActivity";

	private HashMap<String, Business> _allBusinesses;
	private HashMap<String, com.gling.bookmeup.business.Business> _allParseBusinesses;
	private BusinessCardArrayMultiChoiceAdapter _businessesCardAdapter;
	private List<Card> _filteredBusinesses;

	private CardListViewWrapperView _businessesCardListView; 

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView");

		_allBusinesses = new HashMap<String, Business>();
		_allParseBusinesses = new HashMap<String, com.gling.bookmeup.business.Business>();
		_filteredBusinesses = new ArrayList<Card>();
		_businessesCardAdapter = new BusinessCardArrayMultiChoiceAdapter(getActivity(), _filteredBusinesses);

		View view = super.onCreateView(inflater, container, savedInstanceState);
		_businessesCardListView = (CardListViewWrapperView) view.findViewById(R.id.customer_favourites_business_list_listViewBusinesses);
		_businessesCardListView.setAdapter(_businessesCardAdapter);
		_businessesCardListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

		EditText edtSearch = (EditText)view.findViewById(R.id.customer_favourites_business_list_edtSearch);
		edtSearch.addTextChangedListener(this);

		_businessesCardListView.setDisplayMode(DisplayMode.LOADING_VIEW);
		@SuppressWarnings("unchecked")
		ArrayList<ParseObject> favouriteBusinesses = (ArrayList<ParseObject>) Customer.getCurrentCustomer().get(Customer.Keys.FAVOURITES);
		if (favouriteBusinesses == null) {
			_businessesCardListView.setDisplayMode(DisplayMode.NO_ITEMS_VIEW);
		} else {
			for (ParseObject parseObject : favouriteBusinesses) {
				if (parseObject instanceof com.gling.bookmeup.business.Business) {
					com.gling.bookmeup.business.Business businessItem = (com.gling.bookmeup.business.Business) parseObject;
					try {
						ParseObject businessObject = businessItem.fetchIfNeeded();
						businessItem = (com.gling.bookmeup.business.Business) businessObject;

						Business currentBusiness = new Business(businessItem);
						Card businessCard = currentBusiness.toCard(getActivity());
						businessCard.setOnClickListener(new OnCardClickListener() {

							@Override
							public void onClick(Card businessCard, View arg1) {
								if (!_allParseBusinesses.containsKey(businessCard.getId())) {
									Log.i(TAG, "Business Dialog - could not find business");
									Crouton.showText(getActivity(), "Could not find business", Style.ALERT);
									return;
								}
								com.gling.bookmeup.business.Business business = _allParseBusinesses.get(businessCard.getId());
								Log.i(TAG, "Business Dialog - " + business.getName());
								CustomerChooseBusinessDialogs dialog = new CustomerChooseBusinessDialogs();
								dialog.createBusinessProfileDialog(business, getActivity(), getResources(), Customer.getCurrentCustomer());
							}
						});

						if (!_allBusinesses.containsKey(currentBusiness._id)) {						
							_filteredBusinesses.add(businessCard);
							_allBusinesses.put(currentBusiness._id, currentBusiness);
						}

						String id = businessItem.getObjectId();
						if (!_allParseBusinesses.containsKey(id)) {
							_allParseBusinesses.put(id, businessItem);
						}

					} catch (ParseException e) {
						Log.e(TAG, "Exception: " + e.getMessage());
					}
				}
			}
			_businessesCardAdapter.notifyDataSetChanged();
			DisplayMode newDisplayMode = _allBusinesses.isEmpty()? DisplayMode.NO_ITEMS_VIEW : DisplayMode.LIST_VIEW;
			_businessesCardListView.setDisplayMode(newDisplayMode);
		}
		return view;
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

	private static class Business {
		public final String _id, _businessName;

		public Business(com.gling.bookmeup.business.Business business) {
			_id = business.getObjectId();
			_businessName = business.getName();
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
			CardHeader header = new CardHeader(context);
			header.setTitle(_businessName);
			header.setButtonExpandVisible(false);

			Card card = new Card(context);
			card.addCardHeader(header);
			card.setId(_id);

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

	private class BusinessCardArrayMultiChoiceAdapter extends CardArrayAdapter {//CardArrayMultiChoiceAdapter {

		private BusinessFilter _businessFilter;

		public BusinessCardArrayMultiChoiceAdapter(Context context, List<Card> cards) {
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

