package com.gling.bookmeup.business;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.gling.bookmeup.R;
import com.gling.bookmeup.login.LoginMainActivity;
import com.gling.bookmeup.main.ParseHelper;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class BusinessProfileActivity extends ActionBarActivity {

    private static final String TAG = "BusinessProfileActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.business_profile_activity);
       
        if (savedInstanceState != null) {
            return;
        }

        Fragment firstFragment = new BusinessProfileFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.business_profile_container, firstFragment).commit();
        
        // For not showing the keyboard when an editText gets focus on fragment creation
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.business_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
        case R.id.business_profile_action_settings:
            // openSettings();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
