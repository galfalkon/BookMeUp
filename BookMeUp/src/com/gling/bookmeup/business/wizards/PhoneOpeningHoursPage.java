package com.gling.bookmeup.business.wizards;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.tech.freak.wizardpager.model.ModelCallbacks;
import com.tech.freak.wizardpager.model.Page;
import com.tech.freak.wizardpager.model.ReviewItem;

import java.util.ArrayList;

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
				mData.getString(PHONE_DATA_KEY), getKey(), -1));
		dest.add(new ReviewItem("Opening hours", mData
				.getString(OPENING_HOURS_DATA_KEY), getKey(), -1));
	}

	@Override
	public boolean isCompleted() {
		return !TextUtils.isEmpty(mData.getString(PHONE_DATA_KEY));
	}
}
