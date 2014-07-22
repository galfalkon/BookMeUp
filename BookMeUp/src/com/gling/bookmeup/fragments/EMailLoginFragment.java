package com.gling.bookmeup.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.gling.bookmeup.R;
import com.gling.bookmeup.main.OnClickListenerFragment;
import com.gling.bookmeup.main.FragmentsFlowManager;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class EMailLoginFragment extends OnClickListenerFragment {

	private static final String TAG = "EMailLoginFragment";

	private EditText edtUserName, edtPassword;

	public int getFragmentLayoutId() {
		return R.layout.fragment_email_login;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);

		edtPassword = (EditText)view.findViewById(R.id.email_login_edtPassword);
		edtUserName = (EditText)view.findViewById(R.id.email_login_edtUserName);

		return view;
	}

	@Override
	public void onClick(View v) {
		final int viewClickedId = v.getId();

		switch (viewClickedId) {
		case R.id.email_login_btnContinue:
			handleLoginReuest();
			break;
		}
	}

	private void handleLoginReuest() {
		Log.i(TAG, "handleLoginReuest");

		String userName = edtUserName.getText().toString();
		String password = edtPassword.getText().toString();

		Log.i(TAG, "Showing a progress dialog");
		final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Loging in...", "Please wait");

		ParseUser.logInInBackground(userName, password, new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException e) {
				progressDialog.dismiss();
				if (e != null) {
					Log.i(TAG, "Login failed: " + e.getMessage());
					Toast.makeText(getActivity(), "Login failed: " + e.toString(), Toast.LENGTH_SHORT).show();
					return;
				}

				if (!user.getBoolean("emailVerified")) {
					Log.i(TAG, "User hasn't verified Email address");
					Toast.makeText(getActivity(), "Please verifiy your Email address", Toast.LENGTH_SHORT).show();
					// return;
				}

				Log.i(TAG, "Login succeeded");

				FragmentsFlowManager.goToNextFragment(getActivity(), R.id.email_login_btnContinue);
			}
		});
	}
}
