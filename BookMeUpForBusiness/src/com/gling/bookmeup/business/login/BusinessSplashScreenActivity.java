package com.gling.bookmeup.business.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.gling.bookmeup.sharedlib.login.SplashScreenActivity;
import com.gling.bookmeup.sharedlib.parse.Business;
import com.gling.bookmeup.sharedlib.parse.ParseHelper;
import com.gling.bookmeup.sharedlib.parse.ParseHelper.Category;
import com.gling.bookmeup.sharedlib.parse.ParseHelper.User;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.RefreshCallback;

public class BusinessSplashScreenActivity extends SplashScreenActivity
{
	private static final String TAG = "BusinessSplashScreenActivity";

	@Override
	protected Intent getIntentForNextActivity()
	{
		final Intent intent;
		if (ParseHelper.isUserLoggedIn())
		{
			ParseUser user = ParseUser.getCurrentUser();
			Log.i(TAG, "User '" + user.getUsername() + "' is found to be logged in");

			try
			{
				// Fetch current business
				ParseObject businessParseObject = user.getParseObject(User.Keys.BUSINESS_POINTER);
				if (businessParseObject != null)
				{
					Log.i(TAG, "Fetching current business");
					Business currentBusiness = businessParseObject.fetchIfNeeded();
					Category category = currentBusiness.getCategory();
					if (category != null)
					{
						category.fetchIfNeeded();
					}
					Business.setCurrentBusiness(currentBusiness);
				}
			}
			catch (ParseException e)
			{
				Log.e(TAG, "Exception: " + e.getMessage());
				return new Intent(this, BusinessLoginMainActivity.class);
			}

			// Handle push notification if needed
			Bundle extras = getIntent().getExtras();
			if ((extras != null) && extras.containsKey(EXTRA_PUSH_NOTIFICATION_DATA))
			{
				return handlePushNotification();
			}

			if (!ParseHelper.isEmailVerified())
			{
				Log.i(TAG, "User '" + user.getUsername() + "' mail is not verified");
				intent = new Intent(this, BusinessLoginMainActivity.class);
			}
			else if (Business.getCurrentBusiness() != null)
			{
				Log.i(TAG, "User '" + user.getUsername() + "' is associated with a business");

				if (TextUtils.isEmpty(Business.getCurrentBusiness().getName()))
				{
					intent = new Intent(this, BusinessLoginMainActivity.class);
				}
				else
				{
					intent = new Intent(this, BusinessMainActivity.class);
				}
			}
			user.refreshInBackground(new RefreshCallback()
			{
				@Override
				public void done(ParseObject object, ParseException e)
				{
					if (e != null)
					{
						Log.e(TAG, e.getMessage());
					}
					startActivity(intent);
				}
			});
		}
		else
		{
			intent = new Intent(this, BusinessLoginMainActivity.class);
			startActivity(intent);
		}
		return null;
	}

	@Override
	protected Intent handlePushNotification()
	{
		return null;
	}
}
