package com.gling.bookmeup.login;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.gling.bookmeup.R;
import com.gling.bookmeup.business.BusinessMainActivity;
import com.gling.bookmeup.customer.CustomerMainActivity;
import com.gling.bookmeup.main.FragmentsFlowManager;
import com.gling.bookmeup.main.OnClickListenerFragment;
import com.gling.bookmeup.main.ParseHelper.User;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

public class EMailLoginFragment extends OnClickListenerFragment {

    private static final String TAG = "EMailLoginFragment";

    private EditText edtUserName, edtPassword;

    public int getFragmentLayoutId() {
        return R.layout.login_email_fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        edtPassword = (EditText) view.findViewById(R.id.email_login_edtPassword);
        edtUserName = (EditText) view.findViewById(R.id.email_login_edtUserName);

        return view;
    }

    @Override
    public void onClick(View v) {
        final int viewClickedId = v.getId();

        switch (viewClickedId) {
        case R.id.email_login_btnContinue:
            handleLoginReuest();
            break;
        case R.id.email_login_txtForgotPassword:
        	handleForgotPassword();
        	break;
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
    	alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "Positive button click");
				
				String email = emailInput.getText().toString();
    	        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
    	        {
    	        	Toast.makeText(getActivity(), R.string.email_login_reset_password_dialog_invalid_login_toast_message, Toast.LENGTH_SHORT).show();
    	        	return;
    	        }
    	        
    	        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...");
    	        ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
					@Override
					public void done(ParseException e) {
						Log.i(TAG, "requestPasswordResetInBackground done");
						
						progressDialog.dismiss();
						if (e != null)
						{
							Log.e(TAG, "Exception: " + e.getMessage());
							return;
						}
						
						alertDialog.dismiss();
						Toast.makeText(getActivity(), R.string.email_login_reset_password_dialog_toast_message_on_success, Toast.LENGTH_SHORT).show();
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

        String userName = edtUserName.getText().toString();
        String password = edtPassword.getText().toString();

        Log.i(TAG, "Showing a progress dialog");
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Logging in...");
        
        ParseUser.logInInBackground(userName, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    progressDialog.dismiss();
                    Log.i(TAG, "Login failed: " + e.getMessage());
                    Toast.makeText(getActivity(), "Login failed: " + e.toString(), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!user.getBoolean("emailVerified")) {
                    progressDialog.dismiss();
                    Log.i(TAG, "User hasn't verified Email address");
                    Toast.makeText(getActivity(), "Please verifiy your Email address", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.i(TAG, "User '" + user.getUsername() +  "' logged in");
                if (user.getParseObject(User.Keys.BUSINESS_POINTER) != null) {
                    progressDialog.dismiss();
                    Intent intent = new Intent(getActivity(), BusinessMainActivity.class);
                    startActivity(intent);
                    return;
                }
                
                if (user.getParseObject(User.Keys.CUSTOMER_POINTER) != null) {
                    progressDialog.dismiss();
                    Intent intent = new Intent(getActivity(), CustomerMainActivity.class);
                    startActivity(intent);
                    return;
                }
                // loginActivity.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                FragmentsFlowManager.goToNextFragment(getActivity(), R.id.login_container, R.id.email_login_btnContinue);
                progressDialog.dismiss();
            }
        });
    }
}
