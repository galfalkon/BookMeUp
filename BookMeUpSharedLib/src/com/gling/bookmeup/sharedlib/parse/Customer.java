package com.gling.bookmeup.sharedlib.parse;

import java.util.ArrayList;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName(Customer.CLASS_NAME)
public class Customer extends ParseObject {

    public static final String CLASS_NAME = "Customer";

    public static class Keys {
        public static final String ID = "objectId";
        public static final String NAME = "name";
        public static final String FAVOURITES = "favourites";
        public static final String PHONE_NUMBER = "phoneNumber";
    }

    private static Customer _currentCustomer;

    public static void setCurrentCustomer(Customer currentCustomer) {
        _currentCustomer = currentCustomer;
    }

    public static Customer getCurrentCustomer() {
        return _currentCustomer;
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

    public String getPhoneNumber() {
        return getString(Keys.PHONE_NUMBER);
    }

    public void setPhoneNumber(String phoneNumber) {
        put(Keys.PHONE_NUMBER, phoneNumber);
    }

    public void setFavourites(ArrayList<ParseObject> favourites) {
        put(Keys.FAVOURITES, favourites);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Customer && ((Customer) o).getObjectId().equals(getObjectId())) {
            return true;
        }
        return false;
    }
}