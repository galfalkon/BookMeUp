package com.gling.bookmeup.business.fragments;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Services extends JSONObject{
	
	public Services() {
		// TODO Auto-generated constructor stub
	}
	
	public Services putService(String name, String price, String duration) throws JSONException {
		JSONArray details = new JSONArray();
		details.put(price);
		details.put(duration);
		
		this.put(name, details);
		
		return this;
	}
	
	public JSONArray getService(String name) throws JSONException {
		return this.getJSONArray(name);
	}

}
