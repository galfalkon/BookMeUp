package com.gling.bookmeup.main;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.gling.bookmeup.business.BusinessMainActivity;
import com.gling.bookmeup.customer.CustomerMainActivity;

public class PushHandlerActivity extends Activity {
	private static final String TAG = "PushHandlerActivity";
	
	public static enum PushNotificationType 
	{
		MESSAGE_FROM_BUSINESS("com.gling.bookmeup.main.intent.MESSAGE_FROM_BUSINESS", CustomerMainActivity.class),
		OFFER_FROM_BUSINESS("com.gling.bookmeup.main.intent.OFFER_FROM_BUSINESS", CustomerMainActivity.class),
		
		NEW_BOOKING_REQUEST("com.gling.bookmeup.main.intent.NEW_BOOKING_REQUEST", BusinessMainActivity.class);
		
		private final String _action;
		private final Class<? extends Activity> _activity;
		private PushNotificationType(String action, Class<? extends Activity> activity)
		{
			_action = action;
			_activity = activity;
		}
		
		public void putIntoIntent(Intent intent)
		{
			intent.putExtra("pushType", toString());
		}
		
		public static PushNotificationType getFromIntent(Intent intent)
		{
			Log.i(TAG, "getFromIntent");
			
			Bundle extras = intent.getExtras();
			if (extras == null || !extras.containsKey("pushType"))
			{
				Log.e(TAG, "pushType doesn't exist in the given intent");
				return null;
			}
			
			return PushHandlerActivity.PushNotificationType.valueOf(extras.getString("pushType"));
		}
		
		public static PushNotificationType valueOfAction(String action)
		{
			for (PushNotificationType type : PushNotificationType.values())
			{
				if (type._action.equals(action))
				{
					return type;
				}
			}
			
			return null;
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		
      try {
			JSONObject json = new JSONObject(getIntent().getExtras().getString("com.parse.Data"));
			String action = json.getString("action");
			Log.i(TAG, "Push action: " + action);
			
			PushNotificationType pushType = PushNotificationType.valueOfAction(action);
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
