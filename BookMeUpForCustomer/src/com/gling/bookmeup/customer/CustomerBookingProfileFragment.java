package com.gling.bookmeup.customer;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.Card.OnCardClickListener;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.ActionBar;
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
import android.widget.TextView;

import com.gling.bookmeup.main.OnClickListenerFragment;
import com.gling.bookmeup.main.views.BaseListViewWrapperView.DisplayMode;
import com.gling.bookmeup.main.views.CardListViewWrapperView;
import com.gling.bookmeup.sharedlib.parse.Business;
import com.gling.bookmeup.sharedlib.parse.ParseHelper.Category;
import com.gling.bookmeup.sharedlib.parse.Service;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;

public class CustomerBookingProfileFragment extends OnClickListenerFragment implements TextWatcher {

	private static final String TAG = "CustomerBookingProfileFragment";

	private static final String TITLE = "Business Profile";
	
	public static final String OFFER_EXPIRATION_DATE = "offer expiration date";
	public static final String OFFER_DISCOUNT = "offer discount";
	
	private Integer _offerDiscount = null;
	private Serializable _offerExpirationSerializable = null;
	private Date _offerExpirationDate = null;
	
	private CardListViewWrapperView _allServicesView;
	private ServiceCardArrayAdapter _servicesAdapter;
	private ArrayList<Card> _allCategoriesCards;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_allCategoriesCards = new ArrayList<Card>();
		_servicesAdapter = new ServiceCardArrayAdapter(getActivity(), _allCategoriesCards);
		Bundle arguments = getArguments();
		if (arguments != null) {
			_offerDiscount = arguments.getInt(OFFER_DISCOUNT);
			_offerExpirationSerializable = arguments.getSerializable(OFFER_EXPIRATION_DATE);
			if (_offerExpirationSerializable instanceof Date) {
				_offerExpirationDate = (Date) _offerExpirationSerializable;
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView");
		View view = super.onCreateView(inflater, container, savedInstanceState);

		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(TITLE);

		_allServicesView = (CardListViewWrapperView) view.findViewById(R.id.customer_booking_profile_list_listViewServices);
		_allServicesView.setAdapter(_servicesAdapter);


		Activity activity = getActivity();
		if (activity instanceof CustomerMainActivity) {
			CustomerMainActivity customerActivity = (CustomerMainActivity)activity;
			Business business = customerActivity.getChosenBusiness();
			ParseFile image = business.getImageFile();

			final ParseImageView imageView = (ParseImageView) view.findViewById(R.id.customer_booking_profile_businessParseImageView);
			imageView.setPlaceholder(getActivity().getApplicationContext().getResources().getDrawable(R.drawable.ic_person));
			imageView.setParseFile(image);
			imageView.loadInBackground(new GetDataCallback() {
				@Override
				public void done(byte[] data, ParseException e) {
					if (e != null) {
						Log.e(TAG, "" + e.getMessage());
						imageView.setPlaceholder(getActivity().getApplicationContext().getResources().getDrawable(R.drawable.ic_person));
						return;
					}
					Log.i(TAG, "Fetched business image");
				}
			});
			
			TextView nameView = (TextView) view.findViewById(R.id.customer_booking_profile_businessNameText);
			nameView.setText(business.getName());
			
			TextView categoryView = (TextView) view.findViewById(R.id.customer_booking_profile_businessCategoryText);
			Category category = business.getCategory();
			try {
				category.fetchIfNeeded();
				categoryView.setText(category.getName());
			} catch (ParseException e1) {
				Log.e(TAG, "Exception: " + e1.getMessage());
				categoryView.setText("Not Available");
			}
			
			nameView.setTextSize(40);
			categoryView.setTextSize(20);

			inflateListWithAllServices(business);
		}


		//customer_booking_profile_list_listViewServices

		return view;
	}


	@Override
	protected int getFragmentLayoutId() {
		return R.layout.customer_booking_profile_fragment;
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
	}

	private void inflateListWithAllServices(final Business business) {
		ParseQuery<Service> query = new ParseQuery<Service>(Service.CLASS_NAME);
		query.whereEqualTo(Service.Keys.BUSINESS, business);
		_allServicesView.setDisplayMode(DisplayMode.LOADING_VIEW);

		query.findInBackground(new FindCallback<Service>() {
			@Override
			public void done(List<Service> objects, ParseException e) {
				Log.i(TAG, "Done querying services. #objects = " + objects.size());
				if (e != null) {
					Log.e(TAG, "Exception occurred: " + e.getMessage());
					return;
				}

				for (Service service: objects)
				{
					Card card = serviceToCard(service, getActivity());
					card.setOnClickListener(new OnCardClickListener() {

						@Override
						public void onClick(Card card, View view) {
							Intent intent = new Intent(getActivity(), CustomerCalendarActivity.class);
							intent.putExtra(CustomerCalendarActivity.BUSINESS_ID_EXTRA, business.getObjectId());
							intent.putExtra(CustomerCalendarActivity.SERVICE_ID_EXTRA, card.getId());
							if (_offerDiscount != null) {
								intent.putExtra(OFFER_DISCOUNT, _offerDiscount);
							}
							if (_offerExpirationSerializable != null) {
								intent.putExtra(OFFER_EXPIRATION_DATE, _offerExpirationSerializable);
							}
							startActivity(intent);
						}
					});
					_allCategoriesCards.add(card);
				}

				_servicesAdapter.notifyDataSetChanged();
				DisplayMode newDisplayMode = _allCategoriesCards.isEmpty()? DisplayMode.NO_ITEMS_VIEW : DisplayMode.LIST_VIEW;
				_allServicesView.setDisplayMode(newDisplayMode);
			}
		});
	}

	private Card serviceToCard(Service service, Activity activity) {
		Card card = new Card(activity);
		card.setTitle(service.getName());
		card.setId(service.getObjectId());
		//TODO pay attention to check if there is an offer discount to reduce it from the card
		if (_offerDiscount != null && _offerExpirationDate != null) {			
			Log.i(TAG, "There is an offer for discount of: " + _offerDiscount + "% until: " + _offerExpirationDate); 
		}
		return card;
	}

	private class ServiceCardArrayAdapter extends CardArrayAdapter {


		public ServiceCardArrayAdapter(Context context, List<Card> cards) {
			super(context, cards);
		}

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
			//				updateBusinessesDisplayMode();
			DisplayMode newDisplayMode = _allCategoriesCards.isEmpty()? DisplayMode.NO_ITEMS_VIEW : DisplayMode.LIST_VIEW;
			_allServicesView.setDisplayMode(newDisplayMode);
		}
	}

}

