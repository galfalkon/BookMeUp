package com.gling.bookmeup.main;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.gling.bookmeup.business.fragments.Business;
import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseCloud;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.PushService;

public class ParseHelper {
	private static final String TAG = "ParseHelper";
	private static final String PARSE_APPLICATION_ID = "0Uye8FHMnsklraYbqnMDxtg0rbQRKEqZSVO6BHPa";
	private static final String PARSE_CLIENT_KEY = "5dB8I0UZWFaTtYpE3OUn7CWwPzxYxe2yBqE7uhS3";
	
	public static void initialize(Context context) {
		Log.i(TAG, "Initializing Parse");
		
		ParseObject.registerSubclass(Business.class);
		ParseObject.registerSubclass(Booking.class);
		
		Parse.initialize(context, PARSE_APPLICATION_ID, PARSE_CLIENT_KEY);

		// Configure parse push service
		Log.i(TAG, "Configuring parse push service");
		PushService.setDefaultPushCallback(context, MainActivity.class);
		ParseInstallation.getCurrentInstallation().saveInBackground();
	}
	
	public static class Installation {
		public static final String CLASS_NAME = "Installation";

		public static class Keys {
			public static final String ID = "objectId";
			public static final String BUSINESS_POINTER = "businessPointer";
			public static final String CUSTOMER_POINTER = "customerPointer";
		}
	}
	
	public static class CustomerClass {
		public static final String CLASS_NAME = "Customer";

		public static class Keys {
			public static final String ID = "objectId";
			public static final String NAME = "name";
			public static final String FAVOURITES = "favourites";
		}
	}
	
	public static class BusinessClass {
		public final static String CLASS_NAME = "Business";

		public static class Keys {
			public static final String ID = "objectId";
			public static final String USER = "user";
			public static final String NAME = "name";
			public static final String DESCRIPTION = "description";
			public static final String CATEGORY = "category";
			public static final String OPENING_HOURS = "openingHours";
			public static final String IMAGE = "image";
			public static final String SERVICES = "services";
			public static final String OFFERS = "offers";
		}
	}
	
	@ParseClassName(Booking.CLASS_NAME)
	public static class Booking extends ParseObject {
		
		public static final String CLASS_NAME = "Booking";
		
		public static class Keys {
			public static final String ID = "objectId";
			public static final String CUSTOMER_POINTER = "customerPointer";
			public static final String BUSINESS_POINTER = "businessPointer";
			public static final String DATE = "date";
			public static final String SERVICES = "services";
			public static final String IS_APPROVED = "isApproved";
		}
		
		public Booking() {
			// Do not modify the ParseObject
		}
		
		public String getBusinessName() {
			return getParseObject(Keys.BUSINESS_POINTER).getString(BusinessClass.Keys.NAME);			
		}
		
		public String getClientName() {
			return getParseObject(Keys.CUSTOMER_POINTER).getString(CustomerClass.Keys.NAME);
		}
		
		public String getServiceName() {
			return getString(Keys.SERVICES);
		}
		
		public Date getDate() {
			return getDate(Keys.DATE);
		}
		
		public boolean getIsApproved() {
			return getBoolean(Keys.IS_APPROVED);
		}
	}

	public static class BackEndFunctions {
		
		public static class SendMessageToClients {
			private static final String FUNCTION_NAME = "sendMessageToCustomers";
			
			private static class Parameters {
				public static final String BUSINESS_ID = "businessId";
				public static final String CUSTOMER_IDS = "customerIds";
				public static final String MESSAGE = "message";
			}
			
			public static void callInBackground(String businessId, List<String> customerIds, String message, FunctionCallback<String> callback) {
				// Build a parameters object for the back end function
				final Map<String, Object> params = new HashMap<String, Object>();
				params.put(Parameters.BUSINESS_ID, businessId);
				params.put(Parameters.CUSTOMER_IDS, customerIds);
				params.put(Parameters.MESSAGE, message);
				
				ParseCloud.callFunctionInBackground(FUNCTION_NAME, params, callback);
			}
		}
	}
}
