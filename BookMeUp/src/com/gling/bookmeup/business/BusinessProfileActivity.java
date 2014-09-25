package com.gling.bookmeup.business;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
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
    
    public static Business currentBusiness;
    
    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        Log.i(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.business_profile_activity);
       
        final ProgressDialog progressDialog = ProgressDialog.show(this, null, "Loading business...");
        ParseHelper.fetchBusiness(new GetCallback<Business>() {

            @Override
            public void done(Business business, ParseException e) {
                if (e == null) {
                    Log.i(TAG, "Business " + business.getName() + " fetched");
                    currentBusiness = business;
                    progressDialog.dismiss();
                    
                    if (savedInstanceState != null) {
                        return;
                    }
                    
                    Fragment firstFragment = new BusinessProfileFragment();
                    getFragmentManager().beginTransaction().add(R.id.business_profile_container, firstFragment).commit();
                } else {
                    Log.e(TAG, "Exception: " + e.getMessage());
                    progressDialog.dismiss();
                    ParseUser.logOut();
                    Intent intent = new Intent(getApplicationContext(), LoginMainActivity.class);
                    startActivity(intent);
                    //finish(); // TODO check that everything's fine with that.
                }
            }
        });
        
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
