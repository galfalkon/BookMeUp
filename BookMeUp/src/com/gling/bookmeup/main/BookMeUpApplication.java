package com.gling.bookmeup.main;

import android.app.Application;
import android.util.Log;

public class BookMeUpApplication extends Application {

	private static final String TAG = "BookMeUpApplication";

	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate");
		
		super.onCreate();

		ParseHelper.initialize(this);
	}
}
