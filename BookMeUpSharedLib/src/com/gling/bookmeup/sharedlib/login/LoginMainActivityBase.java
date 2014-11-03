package com.gling.bookmeup.sharedlib.login;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;

import com.gling.bookmeup.sharedlib.R;
import com.gling.bookmeup.sharedlib.parse.ParseHelper;
import com.parse.ParseUser;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public abstract class LoginMainActivityBase extends Activity {

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
    	Log.i(TAG, "onCreate");
    	super.onCreate(savedInstanceState);

        setContentView(R.layout.login_main_activity);

        if (savedInstanceState != null) 
        {
            return;
        }
        
        // Check if the user is logged in but hasn't verified his email address yet
        ParseUser user = ParseUser.getCurrentUser();
        if (user != null && !user.getBoolean(ParseHelper.User.Keys.EMAIL_VERIFIED))
        {
        	Crouton.showText(this, R.string.verify_email_address, Style.INFO);
        }

        getFragmentManager().beginTransaction().add(R.id.login_container, new LoginMainFragment()).commit();
    }
    
    @Override
    protected void onDestroy() 
    {
    	Crouton.cancelAllCroutons();
    	super.onDestroy();
    }
    
    public abstract Fragment getEmailLoginFragmentInstance();
    public abstract Fragment getEmailSignUpFragmentInstance();
}
