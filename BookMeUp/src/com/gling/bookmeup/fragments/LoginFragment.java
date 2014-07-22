package com.gling.bookmeup.fragments;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.gling.bookmeup.R;
import com.gling.bookmeup.main.OnClickListenerFragment;
import com.gling.bookmeup.main.FragmentsFlowManager;

public class LoginFragment extends OnClickListenerFragment {
	private static final String TAG = "LoginFragment";
	
	@Override
	protected int getFragmentLayoutId() {
		return R.layout.fragment_login;
	}
	
	@Override
	public void onClick(View v) {		
		switch (v.getId())
		{
		case R.id.login_btnLoginWithGoogle:
			Log.i(TAG, "btnLoginWithGoogle clicked");
			Toast.makeText(getActivity(), "Not implemented", Toast.LENGTH_SHORT).show();
			break;
		case R.id.login_btnLoginWithFacebook:
			Log.i(TAG, "btnLoginWithFacebook clicked");
			Toast.makeText(getActivity(), "Not implemented", Toast.LENGTH_SHORT).show();
			break;
		case R.id.login_btnLoginWithEMail:
			Log.i(TAG, "btnLoginWithEMail clicked");
			FragmentsFlowManager.goToNextFragment(getActivity(), v.getId());
			break;
		case R.id.login_btnSignUp:
			Log.i(TAG, "btnSignUp clicked");
			FragmentsFlowManager.goToNextFragment(getActivity(), v.getId());
			break;
		}
	}
}