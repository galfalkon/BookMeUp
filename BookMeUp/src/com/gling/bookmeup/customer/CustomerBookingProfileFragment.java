package com.gling.bookmeup.customer;

import android.app.ActionBar;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gling.bookmeup.R;
import com.gling.bookmeup.main.BookMeUpApplication;
import com.gling.bookmeup.main.OnClickListenerFragment;
import com.gling.bookmeup.sharedlib.parse.Business;
import com.gling.bookmeup.sharedlib.parse.ParseHelper;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.tech.freak.wizardpager.model.Page;

public class CustomerBookingProfileFragment extends OnClickListenerFragment implements TextWatcher {
	
	private static final String TAG = "CustomerBookingProfileFragment";
	
	private static final String TITLE = "Business Profile";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView");
		View view = super.onCreateView(inflater, container, savedInstanceState);
		
		ActionBar actionBar = getActivity().getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(TITLE);
        
//        final ParseImageView imageView = (ParseImageView) view.findViewById(R.id.parseImageView);
//
//		imageView.setPlaceholder(BookMeUpApplication.getContext().getResources().getDrawable(R.drawable.ic_person));
//		
//		String imageUri = mPage.getData().getString(Page.SIMPLE_DATA_KEY);
//		if (!TextUtils.isEmpty(imageUri)) {
//			imageView.setImageURI(Uri.parse(imageUri));
//		} else {
//			ParseHelper.fetchBusiness(new GetCallback<Business>() {
//				
//				@Override
//				public void done(Business business, ParseException e) {
//					ParseFile imageFile = business.getImageFile();
//					if (imageFile != null) {
//						imageView.setParseFile(imageFile);
//						imageView.loadInBackground();
//					}
//				}
//			});
//		}

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
	
}

