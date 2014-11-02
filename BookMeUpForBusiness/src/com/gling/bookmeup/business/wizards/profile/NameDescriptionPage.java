package com.gling.bookmeup.business.wizards.profile;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.tech.freak.wizardpager.model.ModelCallbacks;
import com.tech.freak.wizardpager.model.Page;
import com.tech.freak.wizardpager.model.ReviewItem;

/**
 * A page asking for a name and a description.
 */
public class NameDescriptionPage extends Page {
    public static final String NAME_DATA_KEY = "name";
    public static final String DESCRIPTION_DATA_KEY = "desc";

    public NameDescriptionPage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    @Override
    public Fragment createFragment() {
        return NameDescriptionFragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem("Business name", mData.getString(NAME_DATA_KEY), getKey(), 1));
        dest.add(new ReviewItem("Description", mData.getString(DESCRIPTION_DATA_KEY), getKey(), 1));
    }

    @Override
    public boolean isCompleted() {
        return !TextUtils.isEmpty(mData.getString(NAME_DATA_KEY))
                && !TextUtils.isEmpty(mData.getString(DESCRIPTION_DATA_KEY));
    }
}
