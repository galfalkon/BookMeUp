package com.gling.bookmeup.business.login;

import com.gling.bookmeup.sharedlib.login.EMailSignUpFragmentBase;
import com.gling.bookmeup.sharedlib.parse.Business;
import com.gling.bookmeup.sharedlib.parse.ParseHelper;
import com.parse.ParseUser;

public class BusinessEmailSignUpFragment extends EMailSignUpFragmentBase
{
	@Override
	protected void createUserDataAfterSignup(ParseUser user) 
	{
		Business business = new Business();
		business.saveInBackground();
		Business.setCurrentBusiness(business);
		
		user.put(ParseHelper.User.Keys.BUSINESS_POINTER, business);
		user.saveInBackground();
	}
}
