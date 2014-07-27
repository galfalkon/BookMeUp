package com.gling.bookmeup.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.gling.bookmeup.R;
import com.gling.bookmeup.business.BusinessActivity;
import com.gling.bookmeup.fragments.LoginFragment;
import com.parse.ParseAnalytics;

public class MainActivity extends ActionBarActivity {
	
	public final static String EXTRA_MESSAGE = "com.gling.bookmeup.MESSAGE";
	private static final String TAG = "MainActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new LoginFragment()).commit();
		}
		
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
        case R.id.action_business_profile_edit:
        	Intent intent = new Intent(this, BusinessActivity.class);
            intent.putExtra(EXTRA_MESSAGE, "hi there");
            startActivity(intent);
            return true;
        case R.id.action_settings:
            // openSettings();
            return true;
        default:
            return super.onOptionsItemSelected(item);
    }
	}
}
