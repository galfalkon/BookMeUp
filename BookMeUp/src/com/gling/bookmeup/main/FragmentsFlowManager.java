package com.gling.bookmeup.main;

import android.app.Activity;
import android.app.Fragment;
import android.util.Log;
import android.util.SparseArray;

import com.gling.bookmeup.R;
import com.gling.bookmeup.business.BusinessImageCaptureFragment;
import com.gling.bookmeup.login.EMailLoginFragment;
import com.gling.bookmeup.login.EMailSignUpFragment;
import com.gling.bookmeup.login.UserTypeSelectionFragment;

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
		
		// login
		_buttonIdsToFragmentName.put(R.id.login_btnLoginWithEMail, EMailLoginFragment.class.getName());
		_buttonIdsToFragmentName.put(R.id.login_btnSignUp, EMailSignUpFragment.class.getName());
		
		// E-Mail sign up
		_buttonIdsToFragmentName.put(R.id.email_signup_btnSignUp, EMailLoginFragment.class.getName());		

		// E-Mail login
		_buttonIdsToFragmentName.put(R.id.email_login_btnLogin, UserTypeSelectionFragment.class.getName());
		
		// Business image upload
		_buttonIdsToFragmentName.put(R.id.business_profile_creation_btnImageUpload, BusinessImageCaptureFragment.class.getName());
	}
	
	public static void goToNextFragment(Activity activity, int container, int buttonClickedId) {
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
		
		activity.getFragmentManager().
		beginTransaction().
		addToBackStack(null).
		replace(container, Fragment.instantiate(activity, _buttonIdsToFragmentName.get(buttonClickedId))).
		commit();
	}
}
