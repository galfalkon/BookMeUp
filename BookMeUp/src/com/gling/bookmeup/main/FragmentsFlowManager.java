package com.gling.bookmeup.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.SparseArray;

import com.gling.bookmeup.R;
import com.gling.bookmeup.business.fragments.BusinessImageCaptureFragment;
import com.gling.bookmeup.business.fragments.BusinessMainScreen;
import com.gling.bookmeup.business.fragments.BusinessProfileCreationFragment;
import com.gling.bookmeup.customer.fragments.CustomerMainScreen;
import com.gling.bookmeup.login.fragments.EMailLoginFragment;
import com.gling.bookmeup.login.fragments.EMailSignUpFragment;
import com.gling.bookmeup.login.fragments.UserTypeSelectionFragment;

/**
 * This class wraps all transitions from the different fragments in the application.
 * This is done by calling to goToNextFragment. This function identified the origin fragment, and the
 * button that has been clicked, and switches to the proper fragment. 
 * @author Gal Falkon
 */
public class FragmentsFlowManager {
	private static final String TAG = "FragmentsFlowManager";

	private static SparseArray<String> _buttonIdsToFragmentName;
	
	private static void initializeFlowTable() {
		_buttonIdsToFragmentName = new SparseArray<String>();
		
		// user_type_selection
		_buttonIdsToFragmentName.put(R.id.user_type_selection_btnBusiness, BusinessProfileCreationFragment.class.getName());
		_buttonIdsToFragmentName.put(R.id.user_type_selection_btnCustomer, CustomerMainScreen.class.getName());
		
		// login
		_buttonIdsToFragmentName.put(R.id.login_btnLoginWithEMail, EMailLoginFragment.class.getName());
		_buttonIdsToFragmentName.put(R.id.login_btnSignUp, EMailSignUpFragment.class.getName());
		
		// E-Mail sign up
		_buttonIdsToFragmentName.put(R.id.email_signup_btnContinue, UserTypeSelectionFragment.class.getName());		
		
		// E-Mail login
		_buttonIdsToFragmentName.put(R.id.email_login_btnContinue, CustomerMainScreen.class.getName());
		
		// Business profile creation
		_buttonIdsToFragmentName.put(R.id.business_profile_creation_btnCreate, BusinessMainScreen.class.getName());
		
		// Business image upload
		_buttonIdsToFragmentName.put(R.id.business_profile_creation_btnImageUpload, BusinessImageCaptureFragment.class.getName());
	}
	
	public static void goToNextFragment(FragmentActivity fragmentActivity, int buttonClickedId) {
		Log.i(TAG, "goToNextFragment");
		
		if (_buttonIdsToFragmentName == null)
		{
			initializeFlowTable();
		}
		
		if (_buttonIdsToFragmentName.indexOfKey(buttonClickedId) < 0)
		{
			Log.i(TAG, "No transition after a click on the given button");
			return;
		}
		
		fragmentActivity.getSupportFragmentManager().
		beginTransaction().
		addToBackStack(null).
		replace(R.id.container, Fragment.instantiate(fragmentActivity, _buttonIdsToFragmentName.get(buttonClickedId))).
		commit();
	}
}
