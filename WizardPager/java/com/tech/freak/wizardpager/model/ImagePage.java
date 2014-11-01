package com.tech.freak.wizardpager.model;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import com.tech.freak.wizardpager.ui.ImageFragment;

public class ImagePage extends TextPage {

	public ImagePage(ModelCallbacks callbacks, String title) {
		super(callbacks, title);
	}

	@Override
	public Fragment createFragment() {
		return ImageFragment.create(getKey());
	}

	public ImagePage setValue(String value) {
		mData.putString(SIMPLE_DATA_KEY, value);
		return this;
	}
	
	@Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem(getTitle(),
                "Review", getKey(), 1));
    }
}
