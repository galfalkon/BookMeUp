package com.gling.bookmeup.main;

import android.app.Application;
import android.util.Log;

import com.gling.bookmeup.business.Business;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.PushService;

public class BookMeUpApplication extends Application {

	private static final String TAG = "BookMeUpApplication";
	private static final String PARSE_APPLICATION_ID = "0Uye8FHMnsklraYbqnMDxtg0rbQRKEqZSVO6BHPa";
	private static final String PARSE_CLIENT_KEY = "5dB8I0UZWFaTtYpE3OUn7CWwPzxYxe2yBqE7uhS3";

	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate");
		
		super.onCreate();

		// Initialize parse
		Log.i(TAG, "Initializing Parse");
		
		ParseObject.registerSubclass(Business.class);
		
		Parse.initialize(this, PARSE_APPLICATION_ID, PARSE_CLIENT_KEY);

		// Configure parse push service
		Log.i(TAG, "Configuring parse push service");
		PushService.setDefaultPushCallback(this, MainActivity.class);
		ParseInstallation.getCurrentInstallation().saveInBackground();
	}
}
