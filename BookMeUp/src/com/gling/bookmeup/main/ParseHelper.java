package com.gling.bookmeup.main;

public class ParseHelper {
	
	public static class ClientsClass {
		public final static String CLASS_NAME = "Clients";
		
		public static class Keys {
			public static final String ID = "objectId";
			public static final String FAVOURITES = "favourites";
		}
	}
	
	public static class BusinessesClass {
		public final static String CLASS_NAME = "Businesses";
		
		public static class Keys {
			public static final String ID = "objectId";
			public static final String USERNAME = "username";
			public static final String DISPLAY_NAME = "displayName";
			public static final String DESCRIPTION = "description";
			public static final String CATEGORY = "category";
			public static final String OPENING_HOURS = "openingHours";
			public static final String IMAGE = "image";
			public static final String SERVICES = "services";
			public static final String OFFERS = "offers";
		}
	}
	
	public static class BookingsClass {
		public final static String CLASS_NAME = "Bookings";
		
		public static class Keys {
			public static final String CLIENT_ID = "clientId";
			public static final String BUSINESS_ID = "businessId";
			public static final String DATE = "date";
			public static final String SERVICES = "services";
			public static final String IS_APPROVED = "isApproved";
		}
	}
}
