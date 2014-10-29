package com.gling.bookmeup.business.login;

import android.app.Fragment;

import com.gling.bookmeup.sharedlib.login.LoginMainActivityBase;

public class BusinessLoginMainActivity extends LoginMainActivityBase
{
	@Override
	public Fragment getEmailLoginFragmentInstance()
	{
		return new BusinessEMailLoginFragment();
	}

	@Override
	public Fragment getEmailSignUpFragmentInstance()
	{
		return new BusinessEmailSignUpFragment();
	}
}
