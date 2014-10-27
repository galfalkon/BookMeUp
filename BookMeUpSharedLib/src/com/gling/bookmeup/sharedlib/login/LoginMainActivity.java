package com.gling.bookmeup.sharedlib.login;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;

import com.gling.bookmeup.sharedlib.R;

import de.keyboardsurfer.android.widget.crouton.Crouton;

public abstract class LoginMainActivity extends Activity {

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
