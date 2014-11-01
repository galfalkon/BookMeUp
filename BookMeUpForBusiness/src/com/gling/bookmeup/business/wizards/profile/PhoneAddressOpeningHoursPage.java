package com.gling.bookmeup.business.wizards.profile;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.tech.freak.wizardpager.model.ModelCallbacks;
import com.tech.freak.wizardpager.model.Page;
import com.tech.freak.wizardpager.model.ReviewItem;

/**
 * A page asking for a phone number and opening hours.
 */
public class PhoneAddressOpeningHoursPage extends Page {
    public static final String PHONE_DATA_KEY = "phone";
    public static final String ADDRESS_DATA_KEY = "address";
    public static final String OPENING_HOURS_DATA_KEY = "opening_hours";

    public PhoneAddressOpeningHoursPage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    @Override
    public Fragment createFragment() {
        return PhoneAddressOpeningHoursFragment.create(getKey());
    }
    
    public PhoneAddressOpeningHoursPage setPhone(String phone) {
        mData.putString(PHONE_DATA_KEY, phone);
        return this;
    }
    
    public PhoneAddressOpeningHoursPage setAddress(String address) {
        mData.putString(ADDRESS_DATA_KEY, address);
        return this;
    }
    
    public PhoneAddressOpeningHoursPage setOpeningHours(String openingHours) {
        mData.putString(OPENING_HOURS_DATA_KEY, openingHours);
        return this;
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem("Phone number", mData.getString(PHONE_DATA_KEY), getKey(), 1));
        dest.add(new ReviewItem("Address", mData.getString(ADDRESS_DATA_KEY), getKey(), 1));
        dest.add(new ReviewItem("Opening hours", mData.getString(OPENING_HOURS_DATA_KEY), getKey(),
                1));
    }

    @Override
    public boolean isCompleted() {
        return !TextUtils.isEmpty(mData.getString(PHONE_DATA_KEY))
                && !TextUtils.isEmpty(mData.getString(ADDRESS_DATA_KEY))
                && !TextUtils.isEmpty(mData.getString(OPENING_HOURS_DATA_KEY));
    }

}
