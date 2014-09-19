package com.gling.bookmeup.main;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class PushHandlerActivity extends Activity {
	private static final String TAG = "PushHandlerActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		
      try {
			JSONObject json = new JSONObject(getIntent().getExtras().getString("com.parse.Data"));
			String action = json.getString("action");
			Log.i(TAG, "Push action: " + action);
			
			PushUtils.PushNotificationType pushType = PushUtils.PushNotificationType.valueOfAction(action);
			// TODO: Check if activity is already running
			Intent intent = new Intent(getApplicationContext(), pushType._activity);
			pushType.putIntoIntent(intent);
			startActivity(intent);
		} catch (JSONException e) {
			Log.e(TAG, "Exception: " + e.getMessage());
			return;
		} 
	}
}
