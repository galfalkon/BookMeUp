package com.gling.bookmeup.login;

import java.util.List;

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
import com.gling.bookmeup.business.Business;
import com.gling.bookmeup.business.BusinessMainActivity;
import com.gling.bookmeup.customer.Customer;
import com.gling.bookmeup.customer.CustomerMainActivity;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class LoginMainActivity extends ActionBarActivity {

    public final static String EXTRA_MESSAGE = "com.gling.bookmeup.MESSAGE";
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main_activity);

        // Track application opens
        ParseAnalytics.trackAppOpened(getIntent());

        if (savedInstanceState != null) {
            return;
        }

        // TODO splash screen
        // TODO separate 'session manager' class
        Intent intent = generateIntent();
        if (intent != null) {
            startActivity(intent);
        } else {
            // user == null || user is not associated with a business or customer objects
            Fragment firstFragment = new LoginFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.container, firstFragment).commit();
    
            // For not showing the keyboard when an editText gets focus on fragment
            // creation
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
    }

    public Intent generateIntent() {
        final ProgressDialog progressDialog = ProgressDialog.show(this, null, "Loading...");
        
        ParseUser user = ParseUser.getCurrentUser();
        if (user == null) {
            progressDialog.dismiss();
            return null;
        }

        Log.i(TAG, "User '" + user.getUsername() +  "' has been found in the system");
        
        Intent intent = null;

        final ParseQuery<Business> queryBusiness = ParseQuery.getQuery(Business.class).whereEqualTo(Business.Keys.USER, user);
        queryBusiness.include(Business.Keys.CATEGORY);

        final ParseQuery<Customer> queryCustomer = ParseQuery.getQuery(Customer.class).whereEqualTo(Customer.Keys.USER, user);
        
        try {
            List<Business> resultBusiness = queryBusiness.find();
            if (!resultBusiness.isEmpty()) {
                Log.i(TAG, "User '" + user.getUsername() +  "' is associated with business '" + resultBusiness.get(0).getName() + "'");
                intent = new Intent(getApplicationContext(), BusinessMainActivity.class);
                intent.putExtra(Business.CLASS_NAME, resultBusiness.get(0));
                progressDialog.dismiss();
                return intent;
            }
            List<Customer> resultCustomer = queryCustomer.find();
            if (!resultCustomer.isEmpty()) {
                Log.i(TAG, "User '" + user.getUsername() +  "' is associated with customer '" + resultCustomer.get(0).getName() + "'");
                intent = new Intent(getApplicationContext(), CustomerMainActivity.class);
                intent.putExtra(Customer.CLASS_NAME, resultCustomer.get(0));
                progressDialog.dismiss();
                return intent;
            }
        } catch (ParseException e) {
            progressDialog.dismiss();
            Log.i(TAG, "Query failed: " + e.getMessage());
            e.printStackTrace();
        }
        
        Log.i(TAG, "User '" + user.getUsername() +  "' is not associated with a business or customer");
        progressDialog.dismiss();
        return intent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
        case R.id.customer_action_settings:
            // openSettings();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
