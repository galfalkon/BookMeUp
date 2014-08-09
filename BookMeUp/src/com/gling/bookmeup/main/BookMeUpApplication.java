package com.gling.bookmeup.main;

import android.app.Application;
import android.util.Log;

import com.gling.bookmeup.business.fragments.Business;
import com.gling.bookmeup.business.fragments.Service;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.PushService;

public class BookMeUpApplication extends Application {

	private static final String TAG = "BookMeUpApplication";

	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate");
		
		super.onCreate();

		// Initialize parse
		Log.i(TAG, "Initializing Parse");
		
		ParseObject.registerSubclass(Business.class);
		ParseObject.registerSubclass(Service.class);
		
		ParseHelper.initialize(this);
	}
}
