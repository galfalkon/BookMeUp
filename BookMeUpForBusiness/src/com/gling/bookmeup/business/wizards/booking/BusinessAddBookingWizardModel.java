package com.gling.bookmeup.business.wizards.booking;

import android.content.Context;

import com.tech.freak.wizardpager.model.AbstractWizardModel;
import com.tech.freak.wizardpager.model.PageList;

public class BusinessAddBookingWizardModel extends AbstractWizardModel {

    public static final String CUSTOMER = "Customer";
    public static final String SERVICE = "Service";
    public static final String DATE = "Date & Time";
    
    public BusinessAddBookingWizardModel(Context context) {
        super(context);
    }

    @Override
    protected PageList onNewRootPageList() {
        return new PageList(
        new CustomerPage(this, CUSTOMER).setRequired(true),
        new ServicePage(this, SERVICE).setRequired(true),
        new DatePage(this, DATE).setRequired(true));
    }
}
