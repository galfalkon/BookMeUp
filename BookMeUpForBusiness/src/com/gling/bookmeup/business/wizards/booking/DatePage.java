package com.gling.bookmeup.business.wizards.booking;

import java.util.ArrayList;

import org.joda.time.DateTime;

import android.support.v4.app.Fragment;

import com.gling.bookmeup.main.Constants;
import com.tech.freak.wizardpager.model.ModelCallbacks;
import com.tech.freak.wizardpager.model.Page;
import com.tech.freak.wizardpager.model.ReviewItem;

/**
 * A page asking for a name and a description.
 */
public class DatePage extends Page {

    public DatePage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    @Override
    public Fragment createFragment() {
        return DateFragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        DateTime date = (DateTime) mData.getSerializable(SIMPLE_DATA_KEY);
        dest.add(new ReviewItem("Date", Constants.DATE_TIME_FORMAT.format(date.toDate()), getKey(), -1));
        dest.add(new ReviewItem("Time", Constants.TIME_FORMAT.format(date.toDate()), getKey(), -1));
    }

    @Override
    public boolean isCompleted() {
        return true;
        // TODO check for booking conflicts
    }
    
    public DatePage setValue(DateTime date) {
        mData.putSerializable(SIMPLE_DATA_KEY, date);
        return this;
    }
}
