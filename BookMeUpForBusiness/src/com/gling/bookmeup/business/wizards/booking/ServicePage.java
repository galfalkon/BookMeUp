package com.gling.bookmeup.business.wizards.booking;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.tech.freak.wizardpager.model.ModelCallbacks;
import com.tech.freak.wizardpager.model.Page;
import com.tech.freak.wizardpager.model.ReviewItem;

public class ServicePage extends Page {

    public static final String SERVICE_NAME = "service_name";
    public static final String SERVICE_ID = "service_id";
    
    public ServicePage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }
    
    @Override
    public Fragment createFragment() {
        return ServiceFragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem(getTitle(), mData.getString(SERVICE_NAME), getKey(), 1));
    }

    @Override
    public boolean isCompleted() {
        return !TextUtils.isEmpty(mData.getString(SERVICE_NAME));
    }
}
