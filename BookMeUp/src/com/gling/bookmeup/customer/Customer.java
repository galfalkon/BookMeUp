package com.gling.bookmeup.customer;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;


@ParseClassName(Customer.CLASS_NAME)
public class Customer extends ParseObject {
	private static final String TAG = "Customer";

    public static final String CLASS_NAME = "Customer";
    
    public static class Keys {
        public static final String ID = "objectId";
        public static final String USER = "user";
        public static final String NAME = "name";
        public static final String FAVOURITES = "favourites";
    }
    
	public Customer() {
        // A default constructor is required.
    }
 
    public String getName() {
        return getString(Keys.NAME);
    }
 
    public void setName(String name) {
        put(Keys.NAME, name);
    }
 
    public ParseUser getUser() {
        return getParseUser(Keys.USER);
    }
 
    public void setUser(ParseUser user) {
        put(Keys.USER, user);
    }
    
}
