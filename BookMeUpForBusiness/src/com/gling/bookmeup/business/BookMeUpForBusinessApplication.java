package com.gling.bookmeup.business;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.gling.bookmeup.business.login.BusinessSplashScreenActivity;
import com.gling.bookmeup.sharedlib.parse.ParseHelper;

public class BookMeUpForBusinessApplication extends Application
{
	private static final String TAG = "BookMeUpForBusinessApplication";
	// http://stackoverflow.com/questions/2002288/static-way-to-get-context-on-android
	private static Context context;
	
	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate");
		
		super.onCreate();
		ParseHelper.initialize(this, BusinessSplashScreenActivity.class);
		BookMeUpForBusinessApplication.context = getApplicationContext();
	}

    public static Context getContext() {
        return BookMeUpForBusinessApplication.context;
    }
}
