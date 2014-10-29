package com.gling.bookmeup.customer.login;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.gling.bookmeup.customer.CustomerMainActivity;
import com.gling.bookmeup.sharedlib.login.EMailLoginFragmentBase;
import com.gling.bookmeup.sharedlib.parse.Customer;
import com.gling.bookmeup.sharedlib.parse.ParseHelper.User;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class CustomerEMailLoginFragment extends EMailLoginFragmentBase {
	
	private static final String TAG = "CustomerEMailLoginFragment";
	
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
			// Fetch current customer
			Log.i(TAG, "Fetching current customer");
			ParseObject customerParseObject = user .getParseObject(User.Keys.CUSTOMER_POINTER);
			if (customerParseObject == null)
			{
				// TODO: Create Customer
				Crouton.showText(getActivity(), "TODO: Create Customer (Current user isn't associated with a Customer instance)", Style.INFO);
				return;
			}
			
			Customer currentCustomer = customerParseObject.fetchIfNeeded();
			Customer.setCurrentCustomer(currentCustomer);
			
			if (TextUtils.isEmpty(Customer.getCurrentCustomer().getName()))
			{
				startActivity(new Intent(getActivity(), CustomerProfileCreationActivity.class));
			}
			else
			{
				startActivity(new Intent(getActivity(), CustomerMainActivity.class));
			}
		} 
		catch (ParseException e) 
		{
			Log.e(TAG, "Exception: " + e.getMessage());
			Crouton.showText(getActivity(), "Login failed: " + e.getMessage(), Style.ALERT);
			return;
		}
	}
}
