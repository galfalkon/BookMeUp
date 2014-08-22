package com.gling.bookmeup.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
        setContentView(R.layout.login_activity);

        // Track application opens
        ParseAnalytics.trackAppOpened(getIntent());
        
        if (savedInstanceState != null) {
            return;
        }

        // TODO splash screen
        ParseUser user = ParseUser.getCurrentUser();

        if (user != null) {
            Intent intent = generateIntent(user);
            if (intent != null) {
                startActivity(intent);
            }
        }
        
        // user == null || no businessId or customerId in shared prefs
   
        Fragment firstFragment = new LoginFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.container, firstFragment).commit();

        // For not showing the keyboard when an editText gets focus on fragment creation
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private Intent generateIntent(ParseUser user) {

        Business business = null;
        Customer customer = null;

        // Get saved prefs
        SharedPreferences sharedPrefs = getSharedPreferences(getString(R.string.preference_file_key),
                Context.MODE_PRIVATE);
        String businessId = sharedPrefs.getString(getString(R.string.shared_businessid), null);
        String customerId = sharedPrefs.getString(getString(R.string.shared_customerid), null);

        if (businessId == null && customerId == null) {
            return null;
        }

        Intent intent = null;
        Bundle bundle = new Bundle();

        if (businessId != null) {

            final ParseQuery<Business> query = ParseQuery.getQuery(Business.class).whereEqualTo(Business.Keys.USER,
                    user);
            query.include(Business.Keys.CATEGORY);

            try {
                business = query.find().get(0);
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putSerializable(Business.CLASS_NAME, business);
            intent = new Intent(getApplicationContext(), BusinessMainActivity.class);
            intent.putExtras(bundle);
            
        } else { // customerId != null

            final ParseQuery<Customer> query = 
                    ParseQuery
                    .getQuery(Customer.class)
                    .whereEqualTo(Customer.Keys.USER, user);

            try {
                customer = query.find().get(0);
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putSerializable(Customer.CLASS_NAME, customer);
            intent = new Intent(getApplicationContext(), CustomerMainActivity.class);
            intent.putExtras(bundle);
        }
        
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
        case R.id.action_settings:
            // openSettings();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
