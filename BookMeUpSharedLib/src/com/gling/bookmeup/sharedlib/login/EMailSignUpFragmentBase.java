package com.gling.bookmeup.sharedlib.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.gling.bookmeup.main.FragmentsManagerUtils;
import com.gling.bookmeup.main.OnClickListenerFragment;
import com.gling.bookmeup.sharedlib.R;
import com.gling.bookmeup.sharedlib.parse.ParseHelper;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public abstract class EMailSignUpFragmentBase extends OnClickListenerFragment implements OnEditorActionListener 
{
    private static final String TAG = "EMailSignUpFragmentBase";

    private EditText _edtUserName, _edtEmail, _edtPassword, _edtPasswordVerification;

    protected abstract void createUserDataAfterSignup(ParseUser user);
    
    public int getFragmentLayoutId() {
        return R.layout.login_email_signup_fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        _edtUserName = (EditText) view.findViewById(R.id.email_signup_edtUserName);
        _edtEmail = (EditText) view.findViewById(R.id.email_signup_edtEmail);
        _edtPassword = (EditText) view.findViewById(R.id.email_signup_edtPassword);
        _edtPasswordVerification = (EditText) view.findViewById(R.id.email_signup_edtPasswordVerification);
        
        _edtPasswordVerification.setOnEditorActionListener(this);

        InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        
        return view;
    }

    @Override
    public void onClick(View v) {
        final int viewClickedId = v.getId();

        if (viewClickedId == R.id.email_signup_btnSignUp) 
        {
        	handleSignupClick();
        	return;
		} else 
		{
			return;
		}
    }
    
    @Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) 
	{
		switch (actionId)
		{
		case EditorInfo.IME_ACTION_DONE:
			handleSignupClick();
			return true;
		default:
			return false;
		}
	}
    
    private void handleSignupClick()
    {
    	if (!validateInput()) 
		{
            return;
        }
    	
    	final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Signing up...");

        final ParseUser user = new ParseUser();
        user.setUsername(_edtUserName.getText().toString());
        user.setEmail(_edtEmail.getText().toString());
        user.setPassword(_edtPassword.getText().toString());

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                progressDialog.dismiss();

                if (e != null) {
                    Log.e(TAG, "Sign up failed: " + e.toString());
                    Crouton.showText(getActivity(), "Sign up failed: " + e.getMessage(), Style.ALERT);
                    return;
                }

                Log.i(TAG, "Sign up is done, associating current installation with current user");
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

                createUserDataAfterSignup(user);
                
                InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                
                Crouton.showText(getActivity(), R.string.verify_email_address, Style.CONFIRM);
                FragmentsManagerUtils.goToNextFragment(getActivity(), R.id.login_container, ((LoginMainActivityBase)getActivity()).getEmailLoginFragmentInstance());
            }
        });
    }
    
    private boolean validateInput() {

        // Reset errors.
        _edtUserName.setError(null);
        _edtEmail.setError(null);
        _edtPassword.setError(null);
        _edtPasswordVerification.setError(null);

        String userName = _edtUserName.getText().toString();
        String email = _edtEmail.getText().toString();
        String password = _edtPassword.getText().toString();
        String passwordVerification = _edtPasswordVerification.getText().toString();

        boolean isValid = true;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            _edtPassword.setError(getString(R.string.error_required_field));
            focusView = _edtPassword;
            isValid = false;
        } else if (password.length() < 4) {
            _edtPassword.setError("Passwords should be at least 4 characters long");
            focusView = _edtPassword;
            isValid = false;
        } else if (!TextUtils.equals(password, passwordVerification)) {
            _edtPasswordVerification.setError("Passwords don't match");
            focusView = _edtPasswordVerification;
            isValid = false;
        }
        
        if (TextUtils.isEmpty(email)) {
            _edtEmail.setError(getString(R.string.error_required_field));
            focusView = _edtEmail;
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _edtEmail.setError("This email address is invalid");
            focusView = _edtEmail;
            isValid = false;
        }
        
        if (TextUtils.isEmpty(userName)) {
            _edtUserName.setError(getString(R.string.error_required_field));
            focusView = _edtUserName;
            isValid = false;
        } else if (userName.length() < 4) {
            _edtUserName.setError("User name should be at least 4 characters long");
            focusView = _edtUserName;
            isValid = false;
        }

        if (!isValid) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }

        return isValid;
    }
}
