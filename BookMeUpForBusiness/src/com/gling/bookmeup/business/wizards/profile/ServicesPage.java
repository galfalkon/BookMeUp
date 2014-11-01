package com.gling.bookmeup.business.wizards.profile;

import java.util.ArrayList;

import android.support.v4.app.Fragment;

import com.tech.freak.wizardpager.model.ModelCallbacks;
import com.tech.freak.wizardpager.model.Page;
import com.tech.freak.wizardpager.model.ReviewItem;

/**
 * A page for editing business services.
 */
public class ServicesPage extends Page {

	public ServicesPage(ModelCallbacks callbacks, String title) {
		super(callbacks, title);
	}

	@Override
	public Fragment createFragment() {
		return ServicesFragment.create(getKey());
	}

	@Override
	public void getReviewItems(ArrayList<ReviewItem> dest) {
		dest.add(new ReviewItem(getTitle(),
				"Review", getKey(), 1));
	}
	
}
