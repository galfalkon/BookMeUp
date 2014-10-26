package com.gling.bookmeup.customer.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.gling.bookmeup.customer.CustomerMainActivity;
import com.gling.bookmeup.sharedlib.login.LoginMainActivity;
import com.gling.bookmeup.sharedlib.login.SplashScreenActivity;
import com.gling.bookmeup.sharedlib.parse.Customer;
import com.gling.bookmeup.sharedlib.parse.ParseHelper;
import com.gling.bookmeup.sharedlib.parse.ParseHelper.User;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class CustomerSplashScreenActivity extends SplashScreenActivity
{
	private static final String TAG = "BusinessSplashScreenActivity";

	@Override
	protected void goToNextActivity()
	{
		if (ParseHelper.isUserLoggedIn()) 
		{
			ParseUser user = ParseUser.getCurrentUser();
			Log.i(TAG, "User '" + user.getUsername() + "' is found to be logged in");

			try {
				// Refresh user
				user.refresh();

				// Fetch current customer
				ParseObject customerParseObject = user
						.getParseObject(User.Keys.CUSTOMER_POINTER);
				if (customerParseObject != null) {
					Log.i(TAG, "Fetching current customer");
					Customer currentCustomer = customerParseObject
							.fetchIfNeeded();
					Customer.setCurrentCustomer(currentCustomer);
				}
			} catch (ParseException e) {
				Log.e(TAG, "Exception: " + e.getMessage());
				startActivity(new Intent(this, CustomerLoginMainActivity.class));
				return;
			}

			// Handle push notification if needed
			Bundle extras = getIntent().getExtras();
			if ((extras != null) && extras.containsKey(EXTRA_PUSH_NOTIFICATION_DATA)) {
				handlePushNotification();
				return;
			}

			if (!ParseHelper.isEmailVerified()) 
			{
				Log.i(TAG, "User '" + user.getUsername() + "' mail is not verified");
				startActivity(new Intent(this, CustomerLoginMainActivity.class));
			}
			else if (Customer.getCurrentCustomer() != null) 
			{
				Log.i(TAG, "User '" + user.getUsername() + "' is associated with a customer");
				startActivity(new Intent(this, CustomerMainActivity.class));
			}
			else 
			{
				Log.i(TAG, "User '" + user.getUsername() + "' is not associated with a business or a customer");
				startActivity(new Intent(this, CustomerLoginMainActivity.class));
			}
		}
		else 
		{
			startActivity(new Intent(this, LoginMainActivity.class));
		}
	}
	
	@Override
	protected void handlePushNotification()
	{
		// TODO
	}
}
