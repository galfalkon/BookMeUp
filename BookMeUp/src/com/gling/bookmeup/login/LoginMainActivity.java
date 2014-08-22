package com.gling.bookmeup.login;

import java.util.List;

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
        Intent intent = generateIntent();
        if (intent != null) {
            startActivity(intent);
            finish();
        }

        // user == null || user is not associated with a business or customer objects

        Fragment firstFragment = new LoginFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.container, firstFragment).commit();

        // For not showing the keyboard when an editText gets focus on fragment
        // creation
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private Intent generateIntent() {

        ParseUser user = ParseUser.getCurrentUser();
        if (user == null) {
            return null;
        }

        Intent intent = null;
        Bundle bundle = new Bundle();

        final ParseQuery<Business> queryBusiness = ParseQuery.getQuery(Business.class).whereEqualTo(Business.Keys.USER, user);
        queryBusiness.include(Business.Keys.CATEGORY);

        final ParseQuery<Customer> queryCustomer = ParseQuery.getQuery(Customer.class).whereEqualTo(Customer.Keys.USER, user);
        
        try {
            List<Business> resultBusiness = queryBusiness.find();
            if (!resultBusiness.isEmpty()) {
                bundle.putSerializable(Business.CLASS_NAME, resultBusiness.get(0));
                intent = new Intent(getApplicationContext(), BusinessMainActivity.class);
                intent.putExtras(bundle);
                return intent;
            }
            List<Customer> resultCustomer = queryCustomer.find();
            if (!resultCustomer.isEmpty()) {
                bundle.putSerializable(Customer.CLASS_NAME, resultCustomer.get(0));
                intent = new Intent(getApplicationContext(), BusinessMainActivity.class);
                intent.putExtras(bundle);
                return intent;
            }
        } catch (ParseException e) {
            e.printStackTrace();
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
