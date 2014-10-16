package com.gling.bookmeup.main;


import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.provider.Settings.Secure;
import android.util.Log;

import com.gling.bookmeup.business.Business;
import com.gling.bookmeup.business.Service;
import com.gling.bookmeup.customer.Customer;
import com.gling.bookmeup.login.SplashScreenActivity;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.PushService;

public class ParseHelper {
	private static final String TAG = "ParseHelper";
	private static final String PARSE_APPLICATION_ID = "0Uye8FHMnsklraYbqnMDxtg0rbQRKEqZSVO6BHPa";
	private static final String PARSE_CLIENT_KEY = "5dB8I0UZWFaTtYpE3OUn7CWwPzxYxe2yBqE7uhS3";
	
	public static boolean isUserLoggedIn() {
	    return (ParseUser.getCurrentUser() != null);
	}
	
	public static boolean isEmailVerified() {
	    return ParseUser.getCurrentUser().getBoolean("emailVerified");
	}
	
	public static void fetchBusiness(GetCallback<Business> callback) {
		// TODO null checks on getCurrentUser()
		ParseUser.getCurrentUser().getParseObject(User.Keys.BUSINESS_POINTER)
				.fetchIfNeededInBackground(callback);
	}
	
	public static void fetchCustomer(GetCallback<Customer> callback) {
		// TODO null checks on getCurrentUser()
		ParseUser.getCurrentUser().getParseObject(User.Keys.CUSTOMER_POINTER).fetchIfNeededInBackground(callback);
	}

	public static void initialize(Context context) {
		Log.i(TAG, "Initializing Parse");

		// Register ParseObject subclasses
		ParseObject.registerSubclass(Business.class);
		ParseObject.registerSubclass(Service.class);
		ParseObject.registerSubclass(Booking.class);
		ParseObject.registerSubclass(Customer.class);
		ParseObject.registerSubclass(Category.class);
		ParseObject.registerSubclass(Offer.class);

		Parse.initialize(context, PARSE_APPLICATION_ID, PARSE_CLIENT_KEY);

		// Configure parse push service
		Log.i(TAG, "Configuring parse push service");
		PushService.setDefaultPushCallback(context, SplashScreenActivity.class);

		ParseInstallation installation = ParseInstallation
				.getCurrentInstallation();
		String androidId = Secure.getString(context.getContentResolver(),
				Secure.ANDROID_ID);
		// http://stackoverflow.com/questions/23815445/at-least-one-id-field-installationid-devicetoken-must-be-specified-in-this-op
		// TODO un-comment
//		installation.put("UniqueId", androidId);
//		installation.saveInBackground(new SaveCallback() {
//			@Override
//			public void done(ParseException e) {
//				if (e != null) {
//					Log.e(TAG, e.getMessage());
//				}
//			}
//		});
	}

	public static class Installation {
		public static final String CLASS_NAME = "Installation";

		public static class Keys {
			public static final String ID = "objectId";
			public static final String USER_POINTER = "userPointer";
		}
	}
	
	public static class User {
		public static final String CLASS_NAME = "User";

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

	// for saving business categories in shared prefs
	public final static String BUSINESS_CATEGORIES = "business_categories";
	
	@ParseClassName(Category.CLASS_NAME)
	public static class Category extends ParseObject {
		public static final String CLASS_NAME = "Category";

		public static class Keys {
			public static final String ID = "objectId";
			public static final String NAME = "name";
			public static final String IMAGE = "image";
		}
		
		public String getName() {
			return getString(Keys.NAME);
		}
		
		public ParseFile getImageFile() {
			return getParseFile(Keys.IMAGE);
		}
		
		public void setImageFile(ParseFile image) {
	        put(Keys.IMAGE, image);
	    }
	}

	@ParseClassName(Booking.CLASS_NAME)
	public static class Booking extends ParseObject {

		public static final String CLASS_NAME = "Booking";

		public static class Keys {
			public static final String ID = "objectId";
			public static final String CUSTOMER_POINTER = "customerPointer";
			public static final String BUSINESS_POINTER = "businessPointer";
			public static final String SERVICE_POINTER = "servicePointer";
			public static final String DATE = "date";
			public static final String STATUS = "status";
		}

		public static class Status {
			public static final int PENDING = 0;
			public static final int APPROVED = 1;
			public static final int CANCELED = 2;
		}
		
		public Booking() {
			// Do not modify the ParseObject
		}

		public Date getDate() {
			return getDate(Keys.DATE);
		}

		public int getStatus() {
			return getInt(Keys.STATUS);
		}

		public void setStatus(int status) {
			put(Keys.STATUS, status);
		}

		public String getBusinessName() {
			return getParseObject(Keys.BUSINESS_POINTER).getString(
					Business.Keys.NAME);
		}

		public String getCustomerId() {
			return getParseObject(Keys.CUSTOMER_POINTER).getObjectId();
		}
		
		public String getCustomerName() {
			return getParseObject(Keys.CUSTOMER_POINTER).getString(
					CustomerClass.Keys.NAME);
		}

		public String getServiceName() {
			return getParseObject(Keys.SERVICE_POINTER).getString(
					Service.Keys.NAME);
		}

		public int getServicePrice() {
			return getParseObject(Keys.SERVICE_POINTER).getInt(
					Service.Keys.PRICE);
		}
	}
	
	@ParseClassName(Offer.CLASS_NAME)
	public static class Offer extends ParseObject {
		public static final String CLASS_NAME = "Offer";
		
		public static class Keys {
			public static final String ID = "objectId";
			public static final String CREATION_DATE = "createdAt";
			public static final String BUSINESS_POINTER = "businessPointer";
			public static final String CUSTOMER_POINTERS = "customerPointers";
			public static final String DISCOUNT = "discount";
			public static final String DURATION = "duration";
			public static final String EXPIRATION_DATE = "expirationData";
		}
		
		public Offer() {
			// Do not modify the ParseObject
		}
		
		public Offer(String businessId, List<String> customerIds, int discount, int durationInWeeks) {
			this();
			put(Keys.BUSINESS_POINTER, ParseObject.createWithoutData(Business.class, businessId));
			
			ParseRelation<ParseObject> customersRelation = getRelation(Keys.CUSTOMER_POINTERS);
			for (String customerId : customerIds)
			{
				customersRelation.add(Customer.createWithoutData(Customer.class, customerId));
			}
			
			put(Keys.DISCOUNT, discount);
			
			Calendar calendar = Calendar.getInstance();
    		calendar.add(Calendar.WEEK_OF_YEAR, durationInWeeks);
			put(Keys.EXPIRATION_DATE, calendar.getTime());
		}
		
		public int getDiscount() {
			return getInt(Keys.DISCOUNT);
		}
		
		public int getDuration() {
			return getInt(Keys.DURATION);
		}
		
		public Date getExpirationData() {
			return getDate(Keys.EXPIRATION_DATE);
		}
		
		public String getBusinessName() {
			ParseObject business = getParseObject(Keys.BUSINESS_POINTER);
			try {
				business.fetchIfNeeded();
			} catch (ParseException e) {
				Log.e(TAG, "Exception: " + e.getMessage());
				return null;
			}
			
			return business.getString(Business.Keys.NAME);
		}
	}
}
