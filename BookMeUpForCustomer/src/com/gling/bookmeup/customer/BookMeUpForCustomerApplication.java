package com.gling.bookmeup.customer;

import android.app.Application;
import android.util.Log;

import com.gling.bookmeup.customer.login.CustomerSplashScreenActivity;
import com.gling.bookmeup.sharedlib.parse.ParseHelper;

public class BookMeUpForCustomerApplication extends Application
{
	private static final String TAG = "BookMeUpForCustomerApplication";

	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate");
		
		super.onCreate();
		ParseHelper.initialize(this, CustomerSplashScreenActivity.class);
	}
}
