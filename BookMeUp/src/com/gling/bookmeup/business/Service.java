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
 
    public Service setBusiness(Business business) {
        put(Keys.BUSINESS, business);
        return this;
    }
    
    public String getName() {
        return getString(Keys.NAME);
    }
 
    public Service setName(String name) {
        put(Keys.NAME, name);
        return this;
    }
    
    public int getPrice() {
    	return getInt(Keys.PRICE);
    }
 
    public Service setPrice(int price) {
        put(Keys.PRICE, price);
        return this;
    }
    
    public int getDuration() {
        return getInt(Keys.DURATION);
    }
 
    public Service setDuration(int duration) {
        put(Keys.DURATION, duration);
        return this;
    }
    
}
