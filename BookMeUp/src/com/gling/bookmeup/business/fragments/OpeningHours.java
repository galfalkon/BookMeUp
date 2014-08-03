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

	public void setDay(Day day, boolean isOpen, int fromHour, int fromMinute,
			int toHour, int toMinute) throws JSONException {
		JSONArray details = new JSONArray();
		details.put(isOpen);
		details.put(fromHour);
		details.put(fromMinute);
		details.put(toHour);
		details.put(toMinute);

		this.put(day.name(), details);
	}

	public JSONArray getDay(Day day) throws JSONException {
		return (JSONArray) this.get(day.name());
	}

	public boolean isOpen(Day day) throws JSONException {
		return ((JSONArray) this.get(day.name())).getBoolean(0);
	}
}
