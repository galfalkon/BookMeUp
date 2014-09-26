package com.gling.bookmeup.business;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName(Service.CLASS_NAME)
public class Service extends ParseObject {

    public static final String CLASS_NAME = "Service";
    
    public static class Keys {
        public final static String BUSINESS = "business";
        public final static String NAME = "name";
        public final static String PRICE = "price";
        public final static String DURATION = "duration";
    }
    
    public Service() {
        // A default constructor is required.
    }
 
    public void setBusiness(Business business) {
        put(Keys.BUSINESS, business);
    }
    
    public String getName() {
        return getString(Keys.NAME);
    }
 
    public void setName(String name) {
        put(Keys.NAME, name);
    }
    
    public String getPrice() {
    	return getNumber(Keys.PRICE).toString();
//        return getString(Keys.PRICE);
    }
 
    public void setPrice(String price) {
        put(Keys.PRICE, price);
    }
    
    public String getDuration() {
        return getString(Keys.DURATION);
    }
 
    public void setDuration(String duration) {
        put(Keys.DURATION, duration);
    }
    
}
