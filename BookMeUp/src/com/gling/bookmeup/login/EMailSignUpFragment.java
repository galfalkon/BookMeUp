package com.gling.bookmeup.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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

    private EditText edtUserName, edtEmail, edtPassword, edtPasswordVerification;

    public int getFragmentLayoutId() {
        return R.layout.login_email_signup_fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        edtEmail = (EditText) view.findViewById(R.id.email_signup_edtEmail);
        edtPassword = (EditText) view.findViewById(R.id.email_signup_edtPassword);
        edtPasswordVerification = (EditText) view.findViewById(R.id.email_signup_edtPasswordVerification);
        edtUserName = (EditText) view.findViewById(R.id.email_signup_edtUserName);

        InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        return view;
    }

    @Override
    public void onClick(View v) {
        final int viewClickedId = v.getId();

        switch (viewClickedId) {
        case R.id.email_signup_btnContinue:
            if (!validateInput()) {
                return;
            }
            break;
        default:
            return;
        }

        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Signing up...");

        final ParseUser user = new ParseUser();
        user.setUsername(edtUserName.getText().toString());
        user.setEmail(edtEmail.getText().toString());
        user.setPassword(edtPassword.getText().toString());

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                progressDialog.dismiss();

                if (e != null) {
                    Log.e(TAG, "Sign up failed: " + e.toString());
                    Toast.makeText(getActivity(), "Sign up failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
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

                Toast.makeText(getActivity(), "Please verify your Email address", Toast.LENGTH_LONG).show();
                FragmentsFlowManager.goToNextFragment(getActivity(), R.id.login_container, viewClickedId);
            }
        });
    }

    private boolean validateInput() {

        // Reset errors.
        edtUserName.setError(null);
        edtEmail.setError(null);
        edtPassword.setError(null);
        edtPasswordVerification.setError(null);

        String userName = edtUserName.getText().toString();
        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();
        String passwordVerification = edtPasswordVerification.getText().toString();

        boolean isValid = true;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            edtPassword.setError("This field is required");
            focusView = edtPassword;
            isValid = false;
        } else if (password.length() < 4) {
            edtPassword.setError("Passwords should be at least 4 characters long");
            focusView = edtPassword;
            isValid = false;
        } else if (!TextUtils.equals(password, passwordVerification)) {
            edtPasswordVerification.setError("Passwords don't match");
            focusView = edtPasswordVerification;
            isValid = false;
        }
        
        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("This field is required");
            focusView = edtEmail;
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("This email address is invalid");
            focusView = edtEmail;
            isValid = false;
        }
        
        if (TextUtils.isEmpty(userName)) {
            edtUserName.setError("This field is required");
            focusView = edtUserName;
            isValid = false;
        } else if (userName.length() < 4) {
            edtUserName.setError("User name should be at least 4 characters long");
            focusView = edtUserName;
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
