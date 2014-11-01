package com.gling.bookmeup.business.wizards.booking;

import android.content.Context;

import com.tech.freak.wizardpager.model.AbstractWizardModel;
import com.tech.freak.wizardpager.model.BranchPage;
import com.tech.freak.wizardpager.model.PageList;

public class BusinessAddBookingWizardModel extends AbstractWizardModel {

    public static final String CUSTOMER_TYPE = "Customer Type";
    public static final String REGULAR_CUSTOMER = "Regular Customer";
    public static final String NEW_CUSTOMER = "New Customer";
    public static final String SERVICE = "Service";
    public static final String DATE = "Date & Time";

    public BusinessAddBookingWizardModel(Context context) {
        super(context);
    }

    @Override
    protected PageList onNewRootPageList() {
        return new PageList(
                new BranchPage(this, CUSTOMER_TYPE).addBranch(REGULAR_CUSTOMER,
                                                                new RegularCustomerPage(this, REGULAR_CUSTOMER)
                                                                                                .setRequired(true))
                                                     .addBranch(NEW_CUSTOMER,
                                                                new NewCustomerPage(this, NEW_CUSTOMER)
                                                                                                .setRequired(true)),
                new ServicePage(this, SERVICE).setRequired(true),
                new DatePage(this, DATE).setRequired(true));
    }
}
