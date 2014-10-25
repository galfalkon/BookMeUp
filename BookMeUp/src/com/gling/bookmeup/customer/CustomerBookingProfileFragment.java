package com.gling.bookmeup.customer;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.Card.OnCardClickListener;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;

import java.util.ArrayList;
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

import com.gling.bookmeup.R;
import com.gling.bookmeup.main.OnClickListenerFragment;
import com.gling.bookmeup.main.views.BaseListViewWrapperView.DisplayMode;
import com.gling.bookmeup.main.views.CardListViewWrapperView;
import com.gling.bookmeup.sharedlib.parse.Business;
import com.gling.bookmeup.sharedlib.parse.Service;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

public class CustomerBookingProfileFragment extends OnClickListenerFragment implements TextWatcher {
	
	private static final String TAG = "CustomerBookingProfileFragment";
	
	private static final String TITLE = "Business Profile";
	
	private CardListViewWrapperView _allServicesView;
	private ServiceCardArrayAdapter _servicesAdapter;
	private ArrayList<Card> _allCategoriesCards;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_allCategoriesCards = new ArrayList<Card>();
		_servicesAdapter = new ServiceCardArrayAdapter(getActivity(), _allCategoriesCards);
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
//			String imageUri = business.getImageFile().getUrl();
			
//			final ParseImageView imageView = (ParseImageView) view.findViewById(R.id.customerParseImageView);

//			imageView.setPlaceholder(BookMeUpApplication.getContext().getResources().getDrawable(R.drawable.ic_person));
//		
////		String imageUri = mPage.getData().getString(Page.SIMPLE_DATA_KEY);
//			if (!TextUtils.isEmpty(imageUri)) {
////				Uri.parse(imageUri);
////				imageView.setImageURI(Uri.parse(imageUri));
//			} else {
////				ParseHelper.fetchBusiness(new GetCallback<Business>() {
////					
////					@Override
////					public void done(Business business, ParseException e) {
////						ParseFile imageFile = business.getImageFile();
////						if (imageFile != null) {
////							imageView.setParseFile(imageFile);
////							imageView.loadInBackground();
////						}
////					}
////				});
//			}

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
	
	private static Card serviceToCard(Service service, Activity activity) {
    	Card card = new Card(activity);
    	card.setTitle(service.getName());
    	card.setId(service.getObjectId());
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

