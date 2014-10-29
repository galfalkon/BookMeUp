package com.gling.bookmeup.business.login;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.gling.bookmeup.business.BusinessMainActivity;
import com.gling.bookmeup.business.wizards.profile.BusinessProfileWizardActivity;
import com.gling.bookmeup.sharedlib.login.EMailLoginFragmentBase;
import com.gling.bookmeup.sharedlib.parse.Business;
import com.gling.bookmeup.sharedlib.parse.ParseHelper.Category;
import com.gling.bookmeup.sharedlib.parse.ParseHelper.User;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class BusinessEMailLoginFragment extends EMailLoginFragmentBase
{
	private static final String TAG = "BusinessEMailLoginFragment";

	@Override
	protected void handleSuccessfulLogin(ParseUser user)
	{
		if (!user.getBoolean("emailVerified"))
		{
			Log.i(TAG, "User hasn't verified Email address");
			Crouton.showText(getActivity(), "Please verifiy your Email address", Style.ALERT);
			return;
		}
		
		try
		{
			// Fetch current business
			Log.i(TAG, "Fetching current business");
			ParseObject businessParseObject = user.getParseObject(User.Keys.BUSINESS_POINTER);
			if (businessParseObject == null)
			{
				// TODO: Create Business
				Crouton.showText(getActivity(), "TODO: Create Business (Current user isn't associated with a Business instance)", Style.INFO);
				return;
			}
			
			Business currentBusiness = businessParseObject.fetchIfNeeded();
			Category category = currentBusiness.getCategory();
			if (category != null)
			{
				category.fetchIfNeeded();
			}
			Business.setCurrentBusiness(currentBusiness);
			
			Intent intent;
			if (TextUtils.isEmpty(currentBusiness.getName()))
			{
				intent = new Intent(getActivity(), BusinessProfileWizardActivity.class);
			}
			else
			{
				intent = new Intent(getActivity(), BusinessMainActivity.class);
			}
			startActivity(intent);
		}
		catch (ParseException e)
		{
			Log.i(TAG, "Exception: " + e.getMessage());
			Crouton.showText(getActivity(), "Login failed: " + e.getMessage(), Style.ALERT);
			return;
		}
	}
}
