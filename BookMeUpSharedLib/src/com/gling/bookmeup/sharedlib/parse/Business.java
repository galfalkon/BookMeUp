package com.gling.bookmeup.sharedlib.parse;

import com.gling.bookmeup.sharedlib.parse.ParseHelper.Category;
import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName(Business.CLASS_NAME)
public class Business extends ParseObject {
    public static final String CLASS_NAME = "Business";

    public static class Keys {
        public static final String ID = "objectId";
        public static final String NAME = "name";
        public static final String CATEGORY = "category";
        public static final String DESCRIPTION = "description";
        public static final String PHONE_NUMBER = "phone_number";
        public static final String ADDRESS = "address";
        public static final String OPENING_HOURS = "openingHours";
        public static final String IMAGE = "image";
    }

    private static Business _currentBusiness;

    public static void setCurrentBusiness(Business currentBusiness) {
        _currentBusiness = currentBusiness;
    }

    public static Business getCurrentBusiness() {
        return _currentBusiness;
    }

    public Business() {
        // A default constructor is required.
    }

    public String getName() {
        return getString(Keys.NAME);
    }

    public Business setName(String name) {
        put(Keys.NAME, name);
        return this;
    }

    public String getDescription() {
        return getString(Keys.DESCRIPTION);
    }

    public Business setDescription(String description) {
        put(Keys.DESCRIPTION, description);
        return this;
    }

    public Category getCategory() {
        return (Category) getParseObject(Keys.CATEGORY);
    }

    public Business setCategory(Category category) {
        put(Keys.CATEGORY, category);
        return this;
    }

    public String getPhoneNumber() {
        return getString(Keys.PHONE_NUMBER);
    }

    public Business setPhoneNumber(String phoneNumber) {
        put(Keys.PHONE_NUMBER, phoneNumber);
        return this;
    }

    public String getAddress() {
        return getString(Keys.ADDRESS);
    }

    public Business setAddress(String address) {
        put(Keys.ADDRESS, address);
        return this;
    }

    public String getOpeningHours() {
        return getString(Keys.OPENING_HOURS);
    }

    public Business setOpeningHours(String openingHours) {
        put(Keys.OPENING_HOURS, openingHours);
        return this;
    }

    public ParseFile getImageFile() {
        return getParseFile(Keys.IMAGE);
    }

    public Business setImageFile(ParseFile image) {
        put(Keys.IMAGE, image);
        return this;
    }

    public void getServices(FindCallback<Service> callback) {
        final ParseQuery<Service> query = ParseQuery.getQuery(Service.class)
                                                    .whereEqualTo(Service.Keys.BUSINESS, this)
                                                    .orderByAscending("updatedAt"); // TODO move string to helper
        query.findInBackground(callback);
    }

    public ParseQuery<Service> getServicesQuery() {
        final ParseQuery<Service> query = ParseQuery.getQuery(Service.class)
                                                    .whereEqualTo(Service.Keys.BUSINESS, this)
                                                    .orderByDescending("updatedAt");

        return query;
    }

    // business.getServices(new FindCallback<Service>() {
    // public void done(List<Service> services, ParseException e) {
    // if (e != null) {
    // Log.e(TAG, "Exception occurred: " + e.getMessage());
    // return;
    // }
    //
    // for (Service s : services) {
    // Map<String, String> map = new HashMap<String, String>();
    // map.put(Service.Keys.NAME, s.getName());
    // map.put(Service.Keys.PRICE, s.getPrice());
    // map.put(Service.Keys.DURATION, s.getDuration());
    // _servicesData.add(map);
    // }
    // });

}
