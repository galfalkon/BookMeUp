package com.gling.bookmeup.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import com.gling.bookmeup.R;
import com.gling.bookmeup.business.BusinessMainActivity;
import com.gling.bookmeup.customer.CustomerMainActivity;
import com.gling.bookmeup.main.ParseHelper;
import com.gling.bookmeup.main.ParseHelper.User;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.RefreshCallback;

public class SplashScreenActivity extends Activity {

    private static final String TAG = "SplashScreenActivity";
    public final static String EXTRA_FIRST_FRAGMENT = "com.gling.bookmeup.FIRST_FRAGMENT";
    public final static String EXTRA_MESSAGE = "com.gling.bookmeup.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_splash_screen_activity);

        // Track application opens
        ParseAnalytics.trackAppOpened(getIntent());
    }
    
    @Override
    protected void onResume() {
        super.onResume();

        goToNextActivityIfConnected();
    }
    
    private void goToNextActivityIfConnected() {
    	if (!isNetworkAvailable())
        {
        	Log.i(TAG, "No internet connection");
        	
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setTitle(R.string.error_no_internet_connection);
        	builder.setIconAttribute(android.R.attr.alertDialogIcon);
        	builder.setPositiveButton(R.string.ok, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Log.i(TAG, "ok click");
					finish();
				}
			});
        	builder.setNegativeButton(R.string.try_again, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Log.i(TAG, "Try Again click");
					goToNextActivityIfConnected();
				}
			});
        	builder.create().show();
        	return;
        }
        
        goToNextActivity();
    }
    
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    
    private void goToNextActivity()
    {
    	final Intent intent;

        if (ParseHelper.isUserLoggedIn()) {
            ParseUser user = ParseUser.getCurrentUser();
            Log.i(TAG, "User '" + user.getUsername() + "' is found to be logged in");

            if (!ParseHelper.isEmailVerified()) {
                Log.i(TAG, "User '" + user.getUsername() + "' mail is not verified");
                intent = new Intent(getApplicationContext(), LoginMainActivity.class);
                intent.putExtra(EXTRA_MESSAGE, "Please verify your Email address\nYour registered mail is: " + user.getEmail());
            } else if (user.getParseObject(User.Keys.BUSINESS_POINTER) != null && 
                    user.getParseObject(User.Keys.CUSTOMER_POINTER) != null) {
                Log.i(TAG, "User '" + user.getUsername() + "' is associated with both customer and business");
                intent = new Intent(getApplicationContext(), LoginMainActivity.class);
                intent.putExtra(EXTRA_FIRST_FRAGMENT, "UserTypeSelectionFragment");
            } else if (user.getParseObject(User.Keys.BUSINESS_POINTER) != null) {
                Log.i(TAG, "User '" + user.getUsername() + "' is associated with a business");
                intent = new Intent(getApplicationContext(), BusinessMainActivity.class);
            } else if (user.getParseObject(User.Keys.CUSTOMER_POINTER) != null) {
                Log.i(TAG, "User '" + user.getUsername() + "' is associated with a customer");
                intent = new Intent(getApplicationContext(), CustomerMainActivity.class);
            } else {
                Log.i(TAG, "User '" + user.getUsername() + "' is not associated with a business or a customer");
                intent = new Intent(getApplicationContext(), LoginMainActivity.class);
            }
            user.refreshInBackground(new RefreshCallback() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if (e != null) {
                        Log.e(TAG, e.getMessage());
                    }
                    startActivity(intent);
                }
            });
        } else {
            intent = new Intent(getApplicationContext(), LoginMainActivity.class);
            startActivity(intent);
        }
    }
}
