package com.gling.bookmeup.business.login;

import android.app.Fragment;

import com.gling.bookmeup.sharedlib.login.LoginMainActivity;

public class BusinessLoginMainActivity extends LoginMainActivity
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
