package com.gling.bookmeup.login;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.gling.bookmeup.R;
import com.gling.bookmeup.main.FragmentsFlowManager;
import com.gling.bookmeup.main.OnClickListenerFragment;
import com.gling.bookmeup.main.ParseHelper;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class EMailSignUpFragment extends OnClickListenerFragment {

	private static final String TAG = "EMailSignUpFragment";
	
	private EditText edtUserName, edtEmail, edtEmailVerification, edtPassword, edtPasswordVerification;

	public int getFragmentLayoutId() {
		return R.layout.login_email_signup_fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		
		edtEmail = (EditText)view.findViewById(R.id.email_signup_edtEmail);
		edtEmailVerification = (EditText)view.findViewById(R.id.email_signup_edtEmailVerification);
		edtPassword = (EditText)view.findViewById(R.id.email_signup_edtPassword);
		edtPasswordVerification = (EditText)view.findViewById(R.id.email_signup_edtPasswordVerification);
		edtUserName = (EditText)view.findViewById(R.id.email_signup_edtUserName);
		
		return view;
	}

	@Override
	public void onClick(View v) {
		final int viewClickedId = v.getId();
		
		switch (viewClickedId) {
		case R.id.email_signup_btnContinue:
			if (!validateInput())
			{
				return;
			}
			break;
		default:
			return;
		}
		
		final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Signing up...", "Please wait");
		
		final ParseUser user = new ParseUser();
		user.setUsername(edtUserName.getText().toString());
		user.setEmail(edtEmail.getText().toString());
		user.setPassword(edtPassword.getText().toString());
		
		user.signUpInBackground(new SignUpCallback() {
			@Override
			public void done(ParseException e) {
				progressDialog.dismiss();
				
				if (e != null) {
					Log.e(TAG, "signup failed: " + e.toString());
					Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
					return;
				}
				
				Log.i(TAG, "signup is done, associating current installation with current user");
				ParseInstallation currentInstallation = ParseInstallation.getCurrentInstallation();
				currentInstallation.put(ParseHelper.Installation.Keys.USER_POINTER, user);
				currentInstallation.saveInBackground(new SaveCallback() {
					
					@Override
					public void done(ParseException e) {
						if (e != null) {
							Log.e(TAG, "Installation saveInBackground failed: " + e.getMessage());
							return;
						}
					}
				});
				
				Toast.makeText(getActivity(), "Please verify your Email address", Toast.LENGTH_SHORT).show();
				FragmentsFlowManager.goToNextFragment(getActivity(), R.id.login_container, viewClickedId);
			}
		});
	}
	
	private boolean validateInput() {
		String email = edtEmail.getText().toString();
		String emailVerification = edtEmailVerification.getText().toString();
		String password = edtPassword.getText().toString();
		String passwordVerification = edtPasswordVerification.getText().toString();
		
		if (!email.equals(emailVerification)) {
			Toast.makeText(getActivity(), "Email doesn't match to Email verification", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
			Toast.makeText(getActivity(), "Invalid Email address", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (password.isEmpty()) {
			Log.i(TAG, "Password is empty");
			Toast.makeText(getActivity(), "Please insert a password", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (!password.equals(passwordVerification)) {
			Log.i(TAG, "Password doesn't match to password verification");
			Toast.makeText(getActivity(), "Password doesn't match to password verification", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}
}
