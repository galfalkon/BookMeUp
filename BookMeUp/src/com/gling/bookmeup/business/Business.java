package com.gling.bookmeup.business;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.gling.bookmeup.main.ParseHelper.Category;
import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;


@ParseClassName(Business.CLASS_NAME)
public class Business extends ParseObject implements Serializable {
	private static final String TAG = "Business";

    public static final String CLASS_NAME = "Business";
    
    public static class Keys {
        public static final String ID = "objectId";
        public static final String USER = "user";
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
 
    public ParseUser getUser() {
        return getParseUser(Keys.USER);
    }
 
    public void setUser(ParseUser user) {
        put(Keys.USER, user);
    }
 
    public String getDescription() {
        return getString(Keys.DESCRIPTION);
    }
 
    public void setDescription(String description) {
        put(Keys.DESCRIPTION, description);
    }
    
    public Category getCategory() {
        return (Category) getParseObject(Keys.CATEGORY);
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

    /*
     * Returns a list of offers that are currently active (i.e. haven't been expired yet)
     */
    public List<Offer> getActiveOffers()  {
    	List<Offer> offers = new ArrayList<Offer>();
    	JSONArray jsonOffers = getJSONArray(Keys.OFFERS);
    	Date today = new Date(); 
    	for (int i = 0; i < jsonOffers.length(); i++) {
    		try {
    			Offer offer = new Offer(jsonOffers.getJSONObject(i));
    			if (offer.getExpiration().after(today)) {
    				offers.add(offer);
    			}
    		} catch (Exception e) {
    			Log.e(TAG, "Exeception occurred while trying to create a JSON object. " + e.getMessage());
    		}
    	} 
    	
    	return offers;
    }
    
    public static class Offer {
    	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yy");
    	
    	private static class Keys {
    		// TODO: Consider supporting offers for a certain service
    		public static final String DISCOUNT = "discount";
    		public static final String DURATION = "duration";
    	}
    	
    	private final int _discount;
    	private final Date _expiration;
    	
    	// TODO: Remove
    	public Offer(int discount, int durationInWeeks) {
    		_discount = discount;
    		Calendar calendar = Calendar.getInstance();
    		calendar.add(Calendar.WEEK_OF_YEAR, durationInWeeks);
    		_expiration = calendar.getTime();
		}
    	
    	public Offer(int discount, Date expiration) {
    		_discount = discount;
    		_expiration = expiration;
    	}
    	
    	public Offer(JSONObject json) throws JSONException, ParseException {
			_discount = json.getInt(Keys.DISCOUNT);
			_expiration = DATE_FORMAT.parse(json.getString(Keys.DURATION));
		}
    	
    	private JSONObject toJSONObject() throws JSONException {
    		JSONObject json = new JSONObject();
    		json.put(Keys.DISCOUNT, _discount);
    		json.put(Keys.DURATION, DATE_FORMAT.format(_expiration));
    		return json;
    	}
    	
    	public int getDiscount() {
    		return _discount;
    	}
    	
    	public Date getExpiration() {
    		return _expiration;
    	}
    	
    	public String getFormattedExpirationDate() {
    		return DATE_FORMAT.format(_expiration);
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
