package com.gling.bookmeup.business.fragments;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OpeningHours extends JSONObject {

	public enum Day {
		SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY
	}

	public OpeningHours() {
		// TODO Auto-generated constructor stub
	}

	public void setDay(Day day, String isOpen, String from,
	        String to) {
		JSONArray details = new JSONArray();
		details.put(isOpen);
		details.put(from);
		details.put(to);

		try {
            this.put(day.name(), details);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}

	public JSONArray getDay(Day day) throws JSONException {
		return (JSONArray) this.get(day.name());
	}

	public boolean isOpen(Day day) throws JSONException {
		return ((JSONArray) this.get(day.name())).getBoolean(0);
	}
}
