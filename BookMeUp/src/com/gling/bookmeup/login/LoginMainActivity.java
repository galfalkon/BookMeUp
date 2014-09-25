package com.gling.bookmeup.login;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.gling.bookmeup.R;

public class LoginMainActivity extends Activity {

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main_activity);

        if (savedInstanceState != null) {
            return;
        }

        Fragment firstFragment;
        Intent intent = getIntent();
        String fragment = intent.getStringExtra(SplashScreenActivity.EXTRA_FIRST_FRAGMENT);
        if (fragment != null) {
            if (fragment.equals("UserTypeSelectionFragment")) {
                firstFragment = new UserTypeSelectionFragment();
            } else {
                firstFragment = new LoginFragment();
            }
        } else {
            firstFragment = new LoginFragment();
        }

        String message = intent.getStringExtra(SplashScreenActivity.EXTRA_MESSAGE);
        if (message != null) {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }

        getFragmentManager().beginTransaction().add(R.id.login_container, firstFragment).commit();
    }
}
