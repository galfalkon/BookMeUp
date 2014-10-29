package com.gling.bookmeup.customer.login;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.gling.bookmeup.customer.CustomerMainActivity;
import com.gling.bookmeup.main.PushUtils;
import com.gling.bookmeup.sharedlib.login.LoginMainActivityBase;
import com.gling.bookmeup.sharedlib.login.SplashScreenActivityBase;
import com.gling.bookmeup.sharedlib.parse.Customer;
import com.gling.bookmeup.sharedlib.parse.ParseHelper;
import com.gling.bookmeup.sharedlib.parse.ParseHelper.User;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class CustomerSplashScreenActivity extends SplashScreenActivityBase
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
				if (TextUtils.isEmpty(Customer.getCurrentCustomer().getName()))
				{
					startActivity(new Intent(this, CustomerProfileCreationActivity.class));
				}
				else
				{
					startActivity(new Intent(this, CustomerMainActivity.class));
				}
			}
			else 
			{
				Log.i(TAG, "User '" + user.getUsername() + "' is not associated with a customer");
				startActivity(new Intent(this, CustomerLoginMainActivity.class));
			}
		}
		else 
		{
			startActivity(new Intent(this, CustomerLoginMainActivity.class));
		}
	}
	
	@Override
	protected void handlePushNotification()
	{
		try 
		{
			JSONObject json = new JSONObject(getIntent().getExtras().getString(EXTRA_PUSH_NOTIFICATION_DATA));
			String action = json.getString("action");
			Log.i(TAG, "Push action: " + action);

			PushUtils.PushNotificationType pushType = PushUtils.PushNotificationType.valueOfAction(action);
			Intent intent = new Intent(getApplicationContext(), CustomerMainActivity.class);
			pushType.putIntoIntent(intent);
			startActivity(intent);
		} 
		catch (JSONException e) 
		{
			Log.e(TAG, "Exception: " + e.getMessage());
			return;
		}
	}
}
