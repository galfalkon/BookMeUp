package com.gling.bookmeup.main;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.provider.Settings.Secure;
import android.util.Log;

import com.gling.bookmeup.business.Business;
import com.gling.bookmeup.business.BusinessMainActivity;
import com.gling.bookmeup.customer.Customer;
import com.gling.bookmeup.login.LoginMainActivity;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SaveCallback;

public class ParseHelper {
    private static final String TAG = "ParseHelper";
    private static final String PARSE_APPLICATION_ID = "0Uye8FHMnsklraYbqnMDxtg0rbQRKEqZSVO6BHPa";
    private static final String PARSE_CLIENT_KEY = "5dB8I0UZWFaTtYpE3OUn7CWwPzxYxe2yBqE7uhS3";

    public static void fetchBusiness(GetCallback<Business> callback) {
        // TODO null checks on getCurrentUser()
        ParseUser.getCurrentUser().getParseObject(Business.CLASS_NAME).fetchIfNeededInBackground(callback);
    }
    
    public static void initialize(Context context) {
        Log.i(TAG, "Initializing Parse");

        // Business
        ParseObject.registerSubclass(Business.class);
        ParseObject.registerSubclass(com.gling.bookmeup.business.Service.class);
        ParseObject.registerSubclass(Booking.class);

        // Customer
        ParseObject.registerSubclass(Customer.class);

        Parse.initialize(context, PARSE_APPLICATION_ID, PARSE_CLIENT_KEY);

        // Configure parse push service
        Log.i(TAG, "Configuring parse push service");
        PushService.setDefaultPushCallback(context, LoginMainActivity.class);

        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
        // http://stackoverflow.com/questions/23815445/at-least-one-id-field-installationid-devicetoken-must-be-specified-in-this-op
        installation.put("UniqueId", androidId);
        installation.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
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

    public static class Category {
        public static final String CLASS_NAME = "Category";

        public static class Keys {
            public static final String ID = "objectId";
            public static final String NAME = "Name"; // TODO lowercase
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
            return getParseObject(Keys.BUSINESS_POINTER).getString(Business.Keys.NAME);
        }

        public String getClientName() {
            return getParseObject(Keys.CUSTOMER_POINTER).getString(CustomerClass.Keys.NAME);
        }

        public String getServiceName() {
            return getParseObject(Keys.SERVICE_POINTER).getString(Service.Keys.NAME);
        }

        public int getServicePrice() {
            return getParseObject(Keys.SERVICE_POINTER).getInt(Service.Keys.PRICE);
        }
    }

    public static class Service {
        public static final String CLASS_NAME = "Service";

        public static class Keys {
            public static final String ID = "objectId";
            public static final String BUSINESS_POINTER = "businessPointer";
            public static final String NAME = "name";
            public static final String PRICE = "price";
            public static final String DURATION = "duration";
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

            public static void callInBackground(String businessId, List<String> customerIds, String message,
                    FunctionCallback<String> callback) {
                // Build a parameters object for the back end function
                final Map<String, Object> params = new HashMap<String, Object>();
                params.put(Parameters.BUSINESS_ID, businessId);
                params.put(Parameters.CUSTOMER_IDS, customerIds);
                params.put(Parameters.MESSAGE, message);

                ParseCloud.callFunctionInBackground(FUNCTION_NAME, params, callback);
            }
        }

        public static class SendOfferToClients {
            private static final String FUNCTION_NAME = "sendOfferToCustomers";

            private static class Parameters {
                public static final String BUSINESS_ID = "businessId";
                public static final String CUSTOMER_IDS = "customerIds";
                public static final String DISCOUNT = "discount";
                public static final String DURATION = "duration";
            }

            public static void callInBackground(String businessId, List<String> customerIds, int discount,
                    int duration, FunctionCallback<String> callback) {
                // Build a parameters object for the back end function
                final Map<String, Object> params = new HashMap<String, Object>();
                params.put(Parameters.BUSINESS_ID, businessId);
                params.put(Parameters.CUSTOMER_IDS, customerIds);
                params.put(Parameters.DISCOUNT, discount);
                params.put(Parameters.DURATION, duration);

                ParseCloud.callFunctionInBackground(FUNCTION_NAME, params, callback);
            }
        }
    }
}
