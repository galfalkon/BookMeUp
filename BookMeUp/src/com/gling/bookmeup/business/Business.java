package com.gling.bookmeup.business;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;


@ParseClassName(Business.CLASS_NAME)
public class Business extends ParseObject implements Serializable{
	private static final String TAG = "Business";

    public static final String CLASS_NAME = "Business";
    
    public static class Keys {
        public static final String ID = "objectId";
        public static final String USER_ID = "userId";
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String CATEGORY = "category";
        public static final String OPENING_HOURS = "openingHours";
        public static final String IMAGE = "image";
        public static final String OFFERS = "offers";
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
 
    public String getUser() {
        return getString(Keys.USER_ID);
    }
 
    public void setUser(String userId) {
        put(Keys.USER_ID, userId);
    }
 
    public String getDescription() {
        return getString(Keys.DESCRIPTION);
    }
 
    public void setDescription(String description) {
        put(Keys.DESCRIPTION, description);
    }
    
    public ParseObject getCategory() {
        return getParseObject(Keys.CATEGORY);
    }
 
    public void setCategory(ParseObject category) {
        put(Keys.CATEGORY, category);
    }
    
    public String getOpeningHours() {
        // TODO refactor
        JSONObject oh = getJSONObject(Keys.OPENING_HOURS);
        if (oh == null) {
            return "";
        }
        
        return oh.toString();
    }
 
    public void setOpeningHours(OpeningHours openingHours) {
        put(Keys.OPENING_HOURS, openingHours.getJson());
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
    
    public void addOffer(Offer offer) {
    	Log.i(TAG, "publishOffer");
    	
    	try {
    		JSONArray jsonOffers = getJSONArray(Keys.OFFERS);
			jsonOffers.put(jsonOffers.length(), offer.toJSONObject());
			saveInBackground();
		} catch (JSONException e) {
			Log.e(TAG, "Exception: " + e.getMessage());
		}
    }
    
    public List<Offer> getOffers()  {
    	List<Offer> offers = new ArrayList<Offer>();
	    	JSONArray jsonOffers = getJSONArray(Keys.OFFERS);
	    	for (int i = 0; i < jsonOffers.length(); i++) {
	    		try {
					offers.add(new Offer(jsonOffers.getJSONObject(i)));
	    		} catch (JSONException e) {
	    			Log.e(TAG, "Exeception occurred while trying to create a JSON object. " + e.getMessage());
	    		}
	    	} 
    	
    	return offers;
    }
    
    public static class Offer {
    	private static class Keys {
    		public static final String DISCOUNT = "discount";
    		public static final String DURATION = "duration";
    	}
    	
    	private final int _discount, _duration;
    	
    	public Offer(int discount, int duration) {
    		_discount = discount;
    		_duration = duration;
    	}
    	
    	public Offer(JSONObject json) throws JSONException {
			_discount = json.getInt(Keys.DISCOUNT);
			_duration = json.getInt(Keys.DURATION);
		}
    	
    	private JSONObject toJSONObject() throws JSONException {
    		JSONObject json = new JSONObject();
    		json.put(Keys.DISCOUNT, _discount);
    		json.put(Keys.DURATION, _duration);
    		return json;
    	}
    	
    	public int getDiscount() {
    		return _discount;
    	}
    	
    	public int getDuration() {
    		return _duration;
    	}
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
