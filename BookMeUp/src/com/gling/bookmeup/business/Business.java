package com.gling.bookmeup.business;

import android.util.Log;

import com.gling.bookmeup.main.ParseHelper.Category;
import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;


@ParseClassName(Business.CLASS_NAME)
public class Business extends ParseObject {
    public static final String CLASS_NAME = "Business";
    
    public static class Keys {
        public static final String ID = "objectId";
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String PHONE_NUMBER = "phone_number";
        public static final String CATEGORY = "category";
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
 
    public void setName(String name) {
        put(Keys.NAME, name);
    }
 
    public String getDescription() {
        return getString(Keys.DESCRIPTION);
    }
 
    public void setDescription(String description) {
        put(Keys.DESCRIPTION, description);
    }
    
    public Category getCategory() {
    	Category category = (Category) getParseObject(Keys.CATEGORY);
    	try {
			category.fetchIfNeeded();
		} catch (ParseException e) {
			Log.e(CLASS_NAME, "Coud not fetch category");
			return null;
		}
    	return category;
//        return getString(Keys.CATEGORY);
    }
	
	public void setCategory(Category category) {
        put(Keys.CATEGORY, category);
    }
	
	public void setCategoryByString(String categoryString) {
		final ParseQuery<Category> query = ParseQuery.getQuery(Category.class).whereEqualTo(
                Category.Keys.NAME, categoryString);
		
		Category category;
		try {
			category = query.getFirst();
		} catch (ParseException e) {
			Log.e(CLASS_NAME, "Could not update category in DB");
			return;
		}
		put(Keys.CATEGORY, category);
	}
	
	public String getPhoneNumber() {
        return getString(Keys.PHONE_NUMBER);
    }
 
    public void setPhoneNumber(String phoneNumber) {
        put(Keys.PHONE_NUMBER, phoneNumber);
    }
    
    public String getOpeningHours() {
    	return getString(Keys.OPENING_HOURS);
    }
 
    public void setOpeningHours(String openingHours) {
        put(Keys.OPENING_HOURS, openingHours);
    }
 
    public ParseFile getImageFile() {
        return getParseFile(Keys.IMAGE);
    }
 
    public void setImageFile(ParseFile image) {
        put(Keys.IMAGE, image);
    }
    
    public void getServices(FindCallback<Service> callback) {
        final ParseQuery<Service> query = ParseQuery.getQuery(Service.class).whereEqualTo(
                Service.Keys.BUSINESS, this).orderByAscending("updatedAt"); // TODO move string to helper

        query.findInBackground(callback);
    }
    
    public ParseQuery<Service> getServicesQuery() {
        final ParseQuery<Service> query = ParseQuery.getQuery(Service.class).whereEqualTo(
                Service.Keys.BUSINESS, this).orderByAscending("updatedAt");

        return query;
    }
    
//        business.getServices(new FindCallback<Service>() {
//            public void done(List<Service> services, ParseException e) {
//                if (e != null) {
//                    Log.e(TAG, "Exception occurred: " + e.getMessage());
//                    return;
//                }
//
//                for (Service s : services) {
//                    Map<String, String> map = new HashMap<String, String>();
//                    map.put(Service.Keys.NAME, s.getName());
//                    map.put(Service.Keys.PRICE, s.getPrice());
//                    map.put(Service.Keys.DURATION, s.getDuration());
//                    _servicesData.add(map);
//                }
//            });

    
}
