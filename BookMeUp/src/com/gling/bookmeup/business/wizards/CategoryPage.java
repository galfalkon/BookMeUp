package com.gling.bookmeup.business.wizards;

import android.support.v4.app.Fragment;

import com.tech.freak.wizardpager.model.ModelCallbacks;
import com.tech.freak.wizardpager.model.SingleFixedChoicePage;

public class CategoryPage extends SingleFixedChoicePage {

	public CategoryPage(ModelCallbacks callbacks, String title) {
		super(callbacks, title);
	}

	@Override
	public Fragment createFragment() {
		return CategoryFragment.create(getKey());
	}
}
