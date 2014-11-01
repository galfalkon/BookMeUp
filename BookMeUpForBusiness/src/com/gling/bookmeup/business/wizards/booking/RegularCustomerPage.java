package com.gling.bookmeup.business.wizards.booking;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.tech.freak.wizardpager.model.ModelCallbacks;
import com.tech.freak.wizardpager.model.Page;
import com.tech.freak.wizardpager.model.ReviewItem;

import java.util.ArrayList;

/**
 * A page for choosing a regular customer.
 */
public class RegularCustomerPage extends Page {

    public static final String CUSTOMER_NAME = "customer_name";
    public static final String CUSTOMER_ID = "customer_id";
    
    public RegularCustomerPage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }
    
    @Override
    public Fragment createFragment() {
        return RegularCustomerFragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem(getTitle(), mData.getString(CUSTOMER_NAME), getKey()).setWeight(1));
    }

    @Override
    public boolean isCompleted() {
        return !TextUtils.isEmpty(mData.getString(CUSTOMER_ID));
    }
}
