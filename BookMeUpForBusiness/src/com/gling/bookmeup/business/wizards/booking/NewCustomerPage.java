package com.gling.bookmeup.business.wizards.booking;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.tech.freak.wizardpager.model.ModelCallbacks;
import com.tech.freak.wizardpager.model.Page;
import com.tech.freak.wizardpager.model.ReviewItem;

import java.util.ArrayList;

/**
 * A page asking for a customer name and phone number.
 */
public class NewCustomerPage extends Page {

    public static final String CUSTOMER_NAME = "customer_name";
    public static final String CUSTOMER_PHONE_NUMBER = "customer_phone_number";

    public NewCustomerPage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    @Override
    public Fragment createFragment() {
        return NewCustomerFragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest
            .add(new ReviewItem("Customer name", mData.getString(CUSTOMER_NAME), getKey(), -1)
                                                                                              .setWeight(1));
        dest.add(new ReviewItem("Phone Number", mData.getString(CUSTOMER_PHONE_NUMBER), getKey(),
                -1).setWeight(1));
    }

    @Override
    public boolean isCompleted() {
        return !TextUtils.isEmpty(mData.getString(CUSTOMER_NAME))
                && !TextUtils.isEmpty(mData.getString(CUSTOMER_PHONE_NUMBER));
    }
}
