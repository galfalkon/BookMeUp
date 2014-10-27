package com.gling.bookmeup.business.login;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.gling.bookmeup.business.BusinessMainActivity;
import com.gling.bookmeup.business.wizards.BusinessProfileWizardActivity;
import com.gling.bookmeup.main.FragmentsManagerUtils;
import com.gling.bookmeup.sharedlib.R;
import com.gling.bookmeup.sharedlib.login.EMailLoginFragment;
import com.gling.bookmeup.sharedlib.parse.Business;
import com.gling.bookmeup.sharedlib.parse.Customer;
import com.gling.bookmeup.sharedlib.parse.ParseHelper;
import com.gling.bookmeup.sharedlib.parse.ParseHelper.Category;
import com.gling.bookmeup.sharedlib.parse.ParseHelper.User;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class BusinessEMailLoginFragment extends EMailLoginFragment
{
	private static final String TAG = "BusinessEMailLoginFragment";

	@Override
	protected void handleSuccessfulLogin(ParseUser user)
	{
		try
		{
			// Fetch current business
			Log.i(TAG, "Fetching current business");
			ParseObject businessParseObject = user.getParseObject(User.Keys.BUSINESS_POINTER);
			if (businessParseObject != null)
			{
				Business currentBusiness = businessParseObject.fetchIfNeeded();
				Category category = currentBusiness.getCategory();
				if (category != null)
				{
					category.fetchIfNeeded();
				}
				Business.setCurrentBusiness(currentBusiness);
			}
		}
		catch (ParseException e)
		{
			Log.i(TAG, "Exception: " + e.getMessage());
			Crouton.showText(getActivity(), "Login failed: " + e.getMessage(), Style.ALERT);
			return;
		}

		if (!user.getBoolean("emailVerified"))
		{
			Log.i(TAG, "User hasn't verified Email address");
			Crouton.showText(getActivity(), "Please verifiy your Email address", Style.ALERT);
		}
		else if (user.getParseObject(User.Keys.BUSINESS_POINTER) != null)
		{
			ParseHelper.fetchBusiness(new GetCallback<Business>()
			{

				@Override
				public void done(Business business, ParseException e)
				{
					if (e == null)
					{
						Intent intent;
						if (TextUtils.isEmpty(business.getName()))
						{
							intent = new Intent(getActivity(), BusinessProfileWizardActivity.class);
						}
						else
						{
							intent = new Intent(getActivity(), BusinessMainActivity.class);
						}
						startActivity(intent);
					}
					else
					{
						Crouton.showText(getActivity(), "Oops, we're having difficulties, please try again...", Style.ALERT);
						Log.i(TAG, "Business fetch failed: " + e.getMessage());
					}
				}
			});
		}
	}
}
