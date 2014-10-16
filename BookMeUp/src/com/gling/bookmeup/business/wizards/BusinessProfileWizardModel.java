package com.gling.bookmeup.business.wizards;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;

import com.gling.bookmeup.R;
import com.gling.bookmeup.main.BookMeUpApplication;
import com.gling.bookmeup.main.ParseHelper;
import com.tech.freak.wizardpager.model.AbstractWizardModel;
import com.tech.freak.wizardpager.model.ImagePage;
import com.tech.freak.wizardpager.model.PageList;
import com.tech.freak.wizardpager.model.SingleFixedChoicePage;

public class BusinessProfileWizardModel extends AbstractWizardModel {

	public static final String GENERAL_INFO = "General Info";
	public static final String CATEGORY = "Category";
	public static final String DETAILS = "Details";
	public static final String IMAGE = "Image";
	public static final String SERVICES = "Services";

	public BusinessProfileWizardModel(Context context) {
		super(context);
	}

	@Override
	protected PageList onNewRootPageList() {
		SharedPreferences sp = BookMeUpApplication.getContext().getSharedPreferences(
				BookMeUpApplication.getContext().getString(R.string.preference_file_key),
				Context.MODE_PRIVATE);
		Set<String> categorySet = new HashSet<String>();
		categorySet = sp.getStringSet(ParseHelper.BUSINESS_CATEGORIES,
				categorySet);
		final String[] categoryArr = categorySet.toArray(new String[categorySet
				.size()]);

		return new PageList(

		new NameDescriptionPage(this, GENERAL_INFO).setRequired(true),

		new CategoryPage(this, CATEGORY).setChoices(categoryArr)
				.setRequired(true),

		new PhoneOpeningHoursPage(this, DETAILS).setRequired(true),

		new ParseImagePage(this, IMAGE),
		
		new ServicesPage(this, SERVICES));
	}
}
