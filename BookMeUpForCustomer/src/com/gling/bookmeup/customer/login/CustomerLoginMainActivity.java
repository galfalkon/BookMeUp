package com.gling.bookmeup.customer.login;

import android.app.Fragment;

import com.gling.bookmeup.sharedlib.login.LoginMainActivityBase;

public class CustomerLoginMainActivity extends LoginMainActivityBase
{
	@Override
	public Fragment getEmailLoginFragmentInstance()
	{
		return new CustomerEMailLoginFragment();
	}
	
	@Override
	public Fragment getEmailSignUpFragmentInstance() 
	{
		return new CustomerEmailSignUpFragment();
	}
}
