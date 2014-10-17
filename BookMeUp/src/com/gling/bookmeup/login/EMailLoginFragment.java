package com.gling.bookmeup.login;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.gling.bookmeup.R;
import com.gling.bookmeup.business.Business;
import com.gling.bookmeup.business.BusinessMainActivity;
import com.gling.bookmeup.business.wizards.BusinessProfileWizardActivity;
import com.gling.bookmeup.customer.Customer;
import com.gling.bookmeup.customer.CustomerMainActivity;
import com.gling.bookmeup.main.FragmentsFlowManager;
import com.gling.bookmeup.main.OnClickListenerFragment;
import com.gling.bookmeup.main.ParseHelper;
import com.gling.bookmeup.main.ParseHelper.Category;
import com.gling.bookmeup.main.ParseHelper.User;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class EMailLoginFragment extends OnClickListenerFragment {

    private static final String TAG = "EMailLoginFragment";

    private EditText _edtUserName, _edtPassword;

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
    public void onClick(View v) {
        final int viewClickedId = v.getId();

        switch (viewClickedId) {
        case R.id.email_login_btnLogin:
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
                if (e != null) {
                    progressDialog.dismiss();
                    Log.i(TAG, "Login failed: " + e.toString());
                    Crouton.showText(getActivity(), "Login failed: " + e.getMessage(), Style.ALERT);
                    return;
                }

                Log.i(TAG, "User '" + user.getUsername() + "' logged in");
                try 
    			{
    				// Fetch current business
    				Log.i(TAG, "Fetching current business");
    				ParseObject businessParseObject = user.getParseObject(User.Keys.BUSINESS_POINTER);
    				if (businessParseObject != null) 
    				{
    					Business currentBusiness = businessParseObject.fetchIfNeeded();
    					Category category = currentBusiness.getCategory();
                        if (category != null) {
                            category.fetchIfNeeded();
                        }
    					Business.setCurrentBusiness(currentBusiness);
    				}
    				
    				// Fetch current customer
    				Log.i(TAG, "Fetching current customer");
    				ParseObject customerParseObject = user.getParseObject(User.Keys.CUSTOMER_POINTER);
    				if (customerParseObject != null) 
    				{
    					Customer currentCustomer = customerParseObject.fetchIfNeeded();
    					Customer.setCurrentCustomer(currentCustomer);
    				}
    			} 
    			catch (ParseException e2) 
    			{
    				Log.e(TAG, "Exception: " + e2.getMessage());
    				Log.i(TAG, "Login failed: " + e.toString());
                    Crouton.showText(getActivity(), "Login failed: " + e.getMessage(), Style.ALERT);
                    return;
    			}

                if (!user.getBoolean("emailVerified")) {
                	progressDialog.dismiss();
                    Log.i(TAG, "User hasn't verified Email address");
                    Crouton.showText(getActivity(), "Please verifiy your Email address", Style.ALERT);
                } else if (user.getParseObject(User.Keys.BUSINESS_POINTER) != null
                        && user.getParseObject(User.Keys.CUSTOMER_POINTER) != null) {
                	progressDialog.dismiss();
                    FragmentsFlowManager.goToNextFragment(getActivity(), R.id.login_container,
                            R.id.email_login_btnLogin);
                } else if (user.getParseObject(User.Keys.BUSINESS_POINTER) != null) {
                	ParseHelper.fetchBusiness(new GetCallback<Business>() {
						
						@Override
						public void done(Business business, ParseException e) {
							progressDialog.dismiss();
							if (e == null) {
								Intent intent;
								if (TextUtils.isEmpty(business.getName())) {
									intent = new Intent(getActivity(), BusinessProfileWizardActivity.class);
								} else {
									intent = new Intent(getActivity(), BusinessMainActivity.class);
								}
								startActivity(intent);
		                    } else {
		                        Crouton.showText(getActivity(), "Oops, we're having difficulties, please try again...", Style.ALERT);
		                        Log.i(TAG, "Business fetch failed: " + e.getMessage());
		                    }
						}
					});
                } else if (user.getParseObject(User.Keys.CUSTOMER_POINTER) != null) {
                	progressDialog.dismiss();
                    Intent intent = new Intent(getActivity(), CustomerMainActivity.class);
                    startActivity(intent);
                } else {
                	progressDialog.dismiss();
                    FragmentsFlowManager.goToNextFragment(getActivity(), R.id.login_container,
                            R.id.email_login_btnLogin);
                }
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
