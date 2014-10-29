package com.gling.bookmeup.business.wizards.profile;

import java.util.List;

import android.content.Context;

import com.gling.bookmeup.sharedlib.parse.Business;
import com.gling.bookmeup.sharedlib.parse.ParseHelper.Category;
import com.tech.freak.wizardpager.model.AbstractWizardModel;
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

        List<Category> categories = Category.getCategories();
        String[] categoryNames = new String[categories.size()];
        for (int i = 0; i < categories.size(); ++i) {
            categoryNames[i] = categories.get(i).getName();
        }

        String categoryName = null;
        Category category = Business.getCurrentBusiness().getCategory();
        if (category != null) {
            categoryName = category.getName();
        }

        return new PageList(

        new NameDescriptionPage(this, GENERAL_INFO).setRequired(true),

        new SingleFixedChoicePage(this, CATEGORY).setChoices(categoryNames)
                                                 .setValue(categoryName)
                                                 .setRequired(true),

        new PhoneOpeningHoursPage(this, DETAILS),

        new ParseImagePage(this, IMAGE),

        new ServicesPage(this, SERVICES));
    }
}
