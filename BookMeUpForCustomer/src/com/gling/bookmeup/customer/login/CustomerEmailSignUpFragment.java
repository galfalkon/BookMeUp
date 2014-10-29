package com.gling.bookmeup.customer.login;

import com.gling.bookmeup.sharedlib.login.EMailSignUpFragmentBase;
import com.gling.bookmeup.sharedlib.parse.Customer;
import com.gling.bookmeup.sharedlib.parse.ParseHelper;
import com.parse.ParseUser;

public class CustomerEmailSignUpFragment extends EMailSignUpFragmentBase
{
	@Override
	protected void createUserDataAfterSignup(ParseUser user) 
	{
		Customer customer = new Customer();
		customer.saveInBackground();
		customer.setCurrentCustomer(customer);
		
		user.put(ParseHelper.User.Keys.CUSTOMER_POINTER, customer);
		user.saveInBackground();
	}
}
