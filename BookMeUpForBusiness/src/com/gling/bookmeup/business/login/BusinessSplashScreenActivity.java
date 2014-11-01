package com.gling.bookmeup.business.login;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.gling.bookmeup.business.BusinessMainActivity;
import com.gling.bookmeup.business.wizards.profile.BusinessProfileWizardActivity;
import com.gling.bookmeup.main.PushUtils;
import com.gling.bookmeup.sharedlib.login.SplashScreenActivityBase;
import com.gling.bookmeup.sharedlib.parse.Business;
import com.gling.bookmeup.sharedlib.parse.ParseHelper;
import com.gling.bookmeup.sharedlib.parse.ParseHelper.Category;
import com.gling.bookmeup.sharedlib.parse.ParseHelper.User;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class BusinessSplashScreenActivity extends SplashScreenActivityBase
{
	private static final String TAG = "BusinessSplashScreenActivity";

	@Override
	protected void goToNextActivity()
	{
		if (!ParseHelper.isUserLoggedIn())
		{
			startActivity(new Intent(this, BusinessLoginMainActivity.class));
			return;
		}
		
		ParseUser user = ParseUser.getCurrentUser();
		Log.i(TAG, "User '" + user.getUsername() + "' is found to be logged in");
		if (!ParseHelper.isEmailVerified())
		{
			Log.i(TAG, "User '" + user.getUsername() + "' mail is not verified");
			startActivity(new Intent(this, BusinessLoginMainActivity.class));
			return;
		}
		
		try
		{
			// Refresh user
			user.refresh();
			
			// Fetch current business
			ParseObject businessParseObject = user.getParseObject(User.Keys.BUSINESS_POINTER);
			if (businessParseObject == null)
			{
				// TODO: Create Business
				Crouton.showText(this, "TODO: Create Business (Current user isn't associated with a Business instance)", Style.INFO);
				return;
			}
			Log.i(TAG, "User '" + user.getUsername() + "' is associated with a business");
			
			Business currentBusiness = businessParseObject.fetchIfNeeded();
			Category category = currentBusiness.getCategory();
			if (category != null)
			{
				category.fetchIfNeeded();
			}
			Business.setCurrentBusiness(currentBusiness);
			
			// Handle push notification if needed
			Bundle extras = getIntent().getExtras();
			if ((extras != null) && extras.containsKey(EXTRA_PUSH_NOTIFICATION_DATA))
			{
				handlePushNotification();
				return;
			}
			
			if (TextUtils.isEmpty(Business.getCurrentBusiness().getName()))
			{
				startActivity(new Intent(this, BusinessProfileWizardActivity.class));
			}
			else
			{
				startActivity(new Intent(this, BusinessMainActivity.class));
			}
		}
		catch (ParseException e)
		{
			Log.e(TAG, "Exception: " + e.getMessage());
			startActivity(new Intent(this, BusinessLoginMainActivity.class));
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
			// TODO: Check if activity is already running
			Intent intent = new Intent(getApplicationContext(), BusinessMainActivity.class);
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
