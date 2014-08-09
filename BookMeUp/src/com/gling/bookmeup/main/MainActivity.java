package com.gling.bookmeup.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.gling.bookmeup.R;
import com.gling.bookmeup.business.fragments.Business;
import com.gling.bookmeup.login.fragments.LoginFragment;
import com.parse.ParseAnalytics;

public class MainActivity extends ActionBarActivity {
	
	public final static String EXTRA_MESSAGE = "com.gling.bookmeup.MESSAGE";
	private static final String TAG = "MainActivity";
	
	// i really think we should split the app into three activities:
	// 1. login, 2. business, 3. client.
	// the login will instantialize the others, while passing the user in an intent or something,
	// then, the business activity for example will hold a private business member which it would share through all fragments 
	private Business _business;
	
	public Business getCurrentBusiness() {
        return _business;
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");

		super.onCreate(savedInstanceState);
		
		_business = new Business();
		
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			
			// Create a new Fragment to be placed in the activity layout
			Fragment firstFragment = new LoginFragment();
            
			// In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());
            
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, firstFragment).commit();
		}

		// For not showing the keyboard when an editText gets focus on fragment creation
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
				
		// Track application opens
		ParseAnalytics.trackAppOpened(getIntent());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
