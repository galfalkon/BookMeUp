package com.gling.bookmeup.business.wizards.profile;

import java.util.ArrayList;

import android.support.v4.app.Fragment;

import com.tech.freak.wizardpager.model.ModelCallbacks;
import com.tech.freak.wizardpager.model.Page;
import com.tech.freak.wizardpager.model.ReviewItem;

/**
 * A page asking for a phone number and opening hours.
 */
public class PhoneOpeningHoursPage extends Page {
	public static final String PHONE_DATA_KEY = "phone";
	public static final String OPENING_HOURS_DATA_KEY = "opening_hours";

	public PhoneOpeningHoursPage(ModelCallbacks callbacks, String title) {
		super(callbacks, title);
	}

	@Override
	public Fragment createFragment() {
		return PhoneOpeningHoursFragment.create(getKey());
	}

	@Override
	public void getReviewItems(ArrayList<ReviewItem> dest) {
		dest.add(new ReviewItem("Phone number",
				mData.getString(PHONE_DATA_KEY), getKey(), 1));
		dest.add(new ReviewItem("Opening hours", mData
				.getString(OPENING_HOURS_DATA_KEY), getKey(), 1));
	}
	
}
