package com.gling.bookmeup.sharedlib.login;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.gling.bookmeup.main.OnClickListenerFragment;
import com.gling.bookmeup.sharedlib.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public abstract class EMailLoginFragment extends OnClickListenerFragment {

    private static final String TAG = "EMailLoginFragment";

    private EditText _edtUserName, _edtPassword;

    protected abstract void handleSuccessfulLogin(ParseUser user);
    
    public int getFragmentLayoutId() {
        return R.layout.login_email_fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        _edtPassword = (EditText) view.findViewById(R.id.email_login_edtPassword);
        _edtUserName = (EditText) view.findViewById(R.id.email_login_edtUserName);

        return view;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
		if (id == R.id.email_login_btnLogin) 
		{
			handleLoginReuest();
		} 
		else if (id == R.id.email_login_txtForgotPassword) 
		{
			handleForgotPassword();
		}
    }

    private void handleForgotPassword() {
        Log.i(TAG, "handleForgotPassword");

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.email_login_reset_password_dialog_title);

        // Set up the input
        final EditText emailInput = new EditText(getActivity());

        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        emailInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailInput.setHint(R.string.email_login_reset_password_dialog_email_hint);

        builder.setView(emailInput);
        builder.setPositiveButton(R.string.ok, null);
        builder.setNegativeButton(R.string.cancel, null);

        final AlertDialog alertDialog = builder.show();
        FrameLayout parent = (FrameLayout) emailInput.getParent();
        parent.setPadding(60, 30, 60, 40);

        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Positive button click");

                String email = emailInput.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    emailInput.setError(getActivity().getString(R.string.error_required_field));
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailInput.setError(getString(R.string.email_login_reset_password_dialog_invalid_login_toast_message));
                    return;
                }

                final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...");
                ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
                    @Override
                    public void done(ParseException e) {
                        Log.i(TAG, "requestPasswordResetInBackground done");

                        progressDialog.dismiss();
                        if (e != null) {
                            Log.e(TAG, "Exception: " + e.getMessage());
                            Crouton.showText(getActivity(), e.getMessage() , Style.ALERT);
                            return;
                        }

                        alertDialog.dismiss();
                        Crouton.showText(getActivity(), R.string.email_login_reset_password_dialog_toast_message_on_success, Style.CONFIRM);
                    }
                });
            }
        });
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Negative button click");
                alertDialog.cancel();
            }
        });
    }
    
    private void handleLoginReuest() {
        Log.i(TAG, "handleLoginReuest");

        clearErrors();
        if (!validateInput())
        {
        	return;
        }
        
        String userName = _edtUserName.getText().toString();
        String password = _edtPassword.getText().toString();
        
        Log.i(TAG, "Showing a progress dialog");
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Logging in...");
        ParseUser.logInInBackground(userName, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
            	progressDialog.dismiss();
                if (e != null) {
                    Log.i(TAG, "Login failed: " + e.toString());
                    Crouton.showText(getActivity(), "Login failed: " + e.getMessage(), Style.ALERT);
                    return;
                }

                Log.i(TAG, "User '" + user.getUsername() + "' logged in");
                handleSuccessfulLogin(user);
            }
        });
    }
    
    private boolean validateInput()
    {
    	View firstInvalidInput = null;
    	
    	String password = _edtPassword.getText().toString();
    	if (TextUtils.isEmpty(password))
    	{
    		_edtPassword.setError(getActivity().getString(R.string.error_required_field));
    		firstInvalidInput = _edtPassword;
    	}
    	
    	String userName = _edtUserName.getText().toString();
    	if (TextUtils.isEmpty(userName))
    	{
    		_edtUserName.setError(getActivity().getString(R.string.error_required_field));
    		firstInvalidInput = _edtUserName;
    	}
    	
    	if (firstInvalidInput != null)
    	{
    		firstInvalidInput.requestFocus();
    	}
    	
    	return (firstInvalidInput == null);
    }
    
    private void clearErrors()
    {
    	_edtUserName.setError(null);
    	_edtPassword.setError(null);
    }
}
