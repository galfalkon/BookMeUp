package com.gling.bookmeup.main;

public class ParseHelper {
	
	public static class ClientsClass {
		public final static String CLASS_NAME = "Clients";
		
		public static class Keys {
			public static final String ID = "objectId";
			public static final String NAME = "name";
			public static final String FAVOURITES = "favourites";
		}
	}
	
	public static class BusinessesClass {
		public final static String CLASS_NAME = "Businesses";
		
		public static class Keys {
			public static final String ID = "objectId";
			public static final String NAME = "name";
			public static final String DESCRIPTION = "description";
			public static final String CATEGORY = "category";
			public static final String OPENING_HOURS = "openingHours";
			public static final String PICTURE = "picture";
			public static final String SERVICES = "services";
			public static final String OFFERS = "offers";
		}
	}
	
	public static class BookingsClass {
		public final static String CLASS_NAME = "Bookings";
		
		public static class Keys {
			public static final String CLIENT_POINTER = "clientPointer";
			public static final String BUSINESS_POINTER = "businessPointer";
			public static final String DATE = "date";
			public static final String SERVICES = "services";
			public static final String IS_APPROVED = "isApproved";
		}
	}
}
