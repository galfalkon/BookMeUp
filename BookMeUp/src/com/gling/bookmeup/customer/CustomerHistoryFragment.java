package com.gling.bookmeup.customer;

import com.gling.bookmeup.R;
import com.gling.bookmeup.main.OnClickListenerFragment;
import com.gling.bookmeup.main.FragmentsFlowManager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

public class CustomerHistoryFragment extends OnClickListenerFragment implements OnClickListener {

	private static final String TAG = "CustomerMainActivity";

	@Override
	protected int getFragmentLayoutId() {
		return R.layout.customer_popular_fragment;
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
	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.customer_main_screen_btnBabrbers:
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
}
