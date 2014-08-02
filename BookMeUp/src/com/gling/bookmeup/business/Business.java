package com.gling.bookmeup.business;

import com.gling.bookmeup.main.ParseHelper.BusinessClass;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName(BusinessClass.CLASS_NAME)
public class Business extends ParseObject {

	public Business() {
        // A default constructor is required.
    }
 
    public String getName() {
        return getString(BusinessClass.Keys.NAME);
    }
 
    public void setName(String name) {
        put(BusinessClass.Keys.NAME, name);
    }
 
    public ParseUser getUser() {
        return getParseUser(BusinessClass.Keys.USER);
    }
 
    public void setUser(ParseUser user) {
        put(BusinessClass.Keys.USER, user);
    }
 
    public String getDescription() {
        return getString(BusinessClass.Keys.DESCRIPTION);
    }
 
    public void setDescription(String description) {
        put(BusinessClass.Keys.DESCRIPTION, description);
    }
    
    public String getCategory() {
        return getString(BusinessClass.Keys.CATEGORY);
    }
 
    public void setCategory(String category) {
        put(BusinessClass.Keys.CATEGORY, category);
    }
    
    public OpeningHours getOpeningHours() {
        return (OpeningHours) getJSONObject(BusinessClass.Keys.OPENING_HOURS);
    }
 
    public void setOpeningHours(OpeningHours openingHours) {
        put(BusinessClass.Keys.OPENING_HOURS, openingHours);
    }
 
    public ParseFile getImageFile() {
        return getParseFile(BusinessClass.Keys.IMAGE);
    }
 
    public void setImageFile(ParseFile image) {
        put(BusinessClass.Keys.IMAGE, image);
    }
    
    public Services getServices() {
        return (Services) getJSONObject(BusinessClass.Keys.SERVICES);
    }
 
    public void setServices(Services services) {
        put(BusinessClass.Keys.SERVICES, services);
    }
    
    // TODO add offers
}
