package com.gling.bookmeup.main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;

public class ParseHelper {

	public static class ClientsClass {
		public static final String CLASS_NAME = "Clients";

		public static class Keys {
			public static final String ID = "objectId";
			public static final String NAME = "name";
			public static final String FAVOURITES = "favourites";
		}
	}

	public static class BusinessesClass {
		public static final String CLASS_NAME = "Businesses";

		public static class Keys {
			public static final String ID = "objectId";
			public static final String USERNAME = "username";
			public static final String NAME = "name";
			public static final String DESCRIPTION = "description";
			public static final String CATEGORY = "category";
			public static final String OPENING_HOURS = "openingHours";
			public static final String IMAGE = "image";
			public static final String SERVICES = "services";
			public static final String OFFERS = "offers";
		}
	}

	public static class BookingsClass {
		public static final String CLASS_NAME = "Bookings";

		public static class Keys {
			public static final String ID = "objectId";
			public static final String CLIENT_POINTER = "clientPointer";
			public static final String BUSINESS_POINTER = "businessPointer";
			public static final String DATE = "date";
			public static final String SERVICES = "services";
			public static final String IS_APPROVED = "isApproved";
		}
	}

	public static class BackEndFunctions {
		public static class SendMessageToClients {
			private static final String FUNCTION_NAME = "sendMessageToClients";
			
			private static class Parameters {
				public static final String BUSINESS_ID = "businessId";
				public static final String CLIENTS_IDS = "clients";
				public static final String MESSAGE = "message";
			}
			
			public static void callInBackground(String businessId, List<String> clientsIds, String message, FunctionCallback<Void> callback) {
				// Build a parameters object for the back end function
				final Map<String, Object> params = new HashMap<String, Object>();
				params.put(Parameters.BUSINESS_ID, businessId);
				params.put(Parameters.CLIENTS_IDS, clientsIds);
				params.put(Parameters.MESSAGE, message);
				
				ParseCloud.callFunctionInBackground(FUNCTION_NAME, params, callback);
			}
		}
	}
}
