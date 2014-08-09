package com.gling.bookmeup.login.fragments;

import com.gling.bookmeup.R;
import com.gling.bookmeup.main.OnClickListenerFragment;
import com.gling.bookmeup.main.FragmentsFlowManager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

public class UserTypeSelectionFragment extends OnClickListenerFragment implements OnClickListener {
	private static final String TAG = "UserTypeSelectionFragment";
	
	public int getFragmentLayoutId()
	{
		return R.layout.login_user_type_selection_fragment;
	}
	
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		super.onCreateView(inflater, container, savedInstanceState);
//		Log.i(TAG, "onCreateView");
//
//		View rootView = inflater.inflate(R.layout.fragment_user_type_selection,
//				container, false);
//		
//		// Set event listeners
//		rootView.findViewById(R.id.user_type_selection_btnBusiness).setOnClickListener(this);
//		rootView.findViewById(R.id.user_type_selection_btnCustomer).setOnClickListener(this);
//
//		return rootView;
//	}

	@Override
	public void onClick(View v) {
		// Currently, all buttons triggers a fragment switch.
		
//		switch (v.getId()) {
//		case R.id.user_type_selection_btnBusiness:
//			Log.i(TAG, "btnBusiness clicked");
//			FragmentsFlowManager.goToNextFragment(getActivity(), v.getId());
//			break;
//		case R.id.user_type_selection_btnCustomer:
//			Log.i(TAG, "btnCustomer clicked");
//			break;
//		}
		
		FragmentsFlowManager.goToNextFragment(getActivity(), v.getId());
	}
}