package com.gling.bookmeup.customer.login;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.gling.bookmeup.customer.CustomerMainActivity;
import com.gling.bookmeup.sharedlib.login.EMailLoginFragment;
import com.gling.bookmeup.sharedlib.parse.Customer;
import com.gling.bookmeup.sharedlib.parse.ParseHelper.User;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class CustomerEMailLoginFragment extends EMailLoginFragment {
	
	private static final String TAG = "CustomerEMailLoginFragment";
	
	@Override
	protected void handleSuccessfulLogin(ParseUser user) 
	{
		try 
		{
			// Fetch current customer
			Log.i(TAG, "Fetching current customer");
			ParseObject customerParseObject = user .getParseObject(User.Keys.CUSTOMER_POINTER);
			if (customerParseObject != null) {
				Customer currentCustomer = customerParseObject.fetchIfNeeded();
				Customer.setCurrentCustomer(currentCustomer);
			}
		} catch (ParseException e) {
			Log.i(TAG, "Exception: " + e.getMessage());
			Crouton.showText(getActivity(), "Login failed: " + e.getMessage(), Style.ALERT);
			return;
		}

		if (!user.getBoolean("emailVerified")) {
			Log.i(TAG, "User hasn't verified Email address");
			Crouton.showText(getActivity(), "Please verifiy your Email address", Style.ALERT);
		} else if (user.getParseObject(User.Keys.CUSTOMER_POINTER) != null) 
		{
			if (TextUtils.isEmpty(Customer.getCurrentCustomer().getName()))
			{
				startActivity(new Intent(getActivity(), CustomerProfileCreationActivity.class));
			}
			else
			{
				startActivity(new Intent(getActivity(), CustomerMainActivity.class));
			}
		}
	}
}
