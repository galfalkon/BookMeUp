package com.gling.bookmeup.main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;

public class ParseHelper {

	public static class Installation {
		public static final String CLASS_NAME = "Installation";

		public static class Keys {
			public static final String ID = "objectId";
			public static final String BUSINESS_POINTER = "businessPointer";
			public static final String CUSTOMER_POINTER = "customerPointer";
		}
	}
	
	public static class CusetomerClass {
		public static final String CLASS_NAME = "Customer";

		public static class Keys {
			public static final String ID = "objectId";
			public static final String NAME = "name";
			public static final String FAVOURITES = "favourites";
		}
	}

	public static class BookingClass {
		public static final String CLASS_NAME = "Booking";

		public static class Keys {
			public static final String ID = "objectId";
			public static final String CUSTOMER_POINTER = "customerPointer";
			public static final String BUSINESS_POINTER = "businessPointer";
			public static final String DATE = "date";
			public static final String SERVICES = "services";
			public static final String IS_APPROVED = "isApproved";
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
