package com.gling.bookmeup.main;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.gling.bookmeup.business.BusinessMainActivity;
import com.gling.bookmeup.customer.Customer;
import com.gling.bookmeup.customer.CustomerMainActivity;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.SendCallback;

public class PushUtils {
	private static final String TAG = "ParseUtils";
	
	public static class Channels {
		public static final String OFFER_FROM_BUSINESS = "OfferFromBusiness";
		public static final String MESSAGE_FROM_BUSINESS = "MessageFromBusiness";
	}
	
	public static enum PushNotificationType 
	{
		MESSAGE_FROM_BUSINESS("com.gling.bookmeup.main.intent.MESSAGE_FROM_BUSINESS", CustomerMainActivity.class),
		OFFER_FROM_BUSINESS("com.gling.bookmeup.main.intent.OFFER_FROM_BUSINESS", CustomerMainActivity.class),
		
		NEW_BOOKING_REQUEST("com.gling.bookmeup.main.intent.NEW_BOOKING_REQUEST", BusinessMainActivity.class);
		
		private final String _action;
		public final Class<? extends Activity> _activity;
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
			
			return PushNotificationType.valueOf(extras.getString("pushType"));
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
	
	public static void sendMessageToCustomers(String businessId, String businessName, List<String> customerIds, String message, SendCallback callback) {
		Log.i(TAG, "sendMessageToCustomers");
		
		String alert = "Message from " + businessName + ": " + message;
		JSONObject json = generatePushData(alert, PushNotificationType.MESSAGE_FROM_BUSINESS);
		if (json == null)
		{
			return;
		}
		
		ParseQuery<Customer> customerQuery = new ParseQuery<Customer>(Customer.CLASS_NAME).
				whereContainedIn("objectId", customerIds);
		
		ParseQuery<ParseInstallation> installationQuery = ParseInstallation.getQuery().
				whereMatchesQuery(ParseHelper.Installation.Keys.CUSTOMER_POINTER, customerQuery);
		
		ParsePush push = new ParsePush();
		push.setQuery(installationQuery);
		push.setData(json);
		push.sendInBackground(callback);
	}
	
	public static void sendOfferToCustomers(String businessId,  String businessName, List<String> customerIds, int discount, int duration, SendCallback callback)
	{
		Log.i(TAG, "sendOfferToCustomers");
		
		String alert = "Offer from " + businessName + "! Discount off "
				+ discount + "% for the next ";
		if (duration == 1) {
			alert += "week!";
		} else {
			alert += duration + " weeks!";
		}
		JSONObject json = generatePushData(alert, PushNotificationType.OFFER_FROM_BUSINESS);
		if (json == null)
		{
			return;
		}
		
		ParseQuery<Customer> customerQuery = new ParseQuery<Customer>(Customer.CLASS_NAME).
				whereContainedIn("objectId", customerIds);
		
		ParseQuery<ParseInstallation> installationQuery = ParseInstallation.getQuery().
				whereMatchesQuery(ParseHelper.Installation.Keys.CUSTOMER_POINTER, customerQuery);
		
		ParsePush push = new ParsePush();
		push.setQuery(installationQuery);
		push.setData(json);
		push.sendInBackground(callback);
	}
	
	private static JSONObject generatePushData(String alert, PushNotificationType type) {
		Log.i(TAG, "generatePushData");
		try
		{
			return new JSONObject().
				put("action", type._action).
				put("title", "BookMeUp").
				put("alert", alert);
		}
		catch (JSONException e)
		{
			Log.e(TAG, "Exception: " + e.getMessage());
			return null;
		}
	}
}
