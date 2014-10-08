package com.gling.bookmeup.main;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class BookMeUpApplication extends Application {

	private static final String TAG = "BookMeUpApplication";
	private static Context mContext;
	
	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate");
		
		super.onCreate();
		
		mContext = this;
		ParseHelper.initialize(this);
	}
	
	public static Context getContext(){
        return mContext;
    }
}
