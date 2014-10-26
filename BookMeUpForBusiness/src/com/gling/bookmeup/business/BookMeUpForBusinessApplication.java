package com.gling.bookmeup.business;

import android.app.Application;
import android.util.Log;

import com.gling.bookmeup.business.login.BusinessSplashScreenActivity;
import com.gling.bookmeup.sharedlib.parse.ParseHelper;

public class BookMeUpForBusinessApplication extends Application
{
	private static final String TAG = "BookMeUpForBusinessApplication";

	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate");
		
		super.onCreate();
		ParseHelper.initialize(this, BusinessSplashScreenActivity.class);
	}
}
