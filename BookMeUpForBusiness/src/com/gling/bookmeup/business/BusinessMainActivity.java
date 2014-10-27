package com.gling.bookmeup.business;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.gling.bookmeup.business.login.BusinessLoginMainActivity;
import com.gling.bookmeup.business.wizards.BusinessProfileWizardActivity;
import com.gling.bookmeup.main.NavigationDrawerActivity;
import com.gling.bookmeup.main.PushUtils;
import com.gling.bookmeup.sharedlib.login.LoginMainActivity;
import com.gling.bookmeup.sharedlib.parse.Business;
import com.gling.bookmeup.sharedlib.parse.ParseHelper;
import com.parse.ParseUser;

import de.keyboardsurfer.android.widget.crouton.Crouton;

public class BusinessMainActivity extends NavigationDrawerActivity {
	private static final String TAG = "BusinessMainActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		
		if (Business.getCurrentBusiness() == null)
		{
			ParseUser.logOut();
			Intent intent = new Intent(getApplicationContext(), LoginMainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
		}
		
		PushUtils.PushNotificationType pushType = PushUtils.PushNotificationType.getFromIntent(getIntent());
  		if (pushType != null) {
  			Log.i(TAG, pushType.toString());
  			switch (pushType) {
  			case NEW_BOOKING_REQUEST:
  				onNavigationDrawerItemSelected(0);
  				break;
  			default:
  				Log.e(TAG, "Invalid push type");
  			}
  		}
	}
	
	@Override
	protected void onDestroy() {
		Crouton.cancelAllCroutons();
		super.onDestroy();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.business_action_profile:
			intent = new Intent(this, BusinessProfileWizardActivity.class);
			startActivity(intent);
			return true;
		case R.id.business_action_calendar:
			intent = new Intent(this, BusinessCalendarActivity.class);
			startActivity(intent);
			return true;
		case R.id.business_action_logout:
			// TODO extract to session manager class
		    ParseHelper.logOut();
			intent = new Intent(this, BusinessLoginMainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public String[] getSectionTitles() {
		return getResources().getStringArray(R.array.business_navigation_drawer_items);
	}

	@Override
	protected Fragment getSectionFragment(int position) {
		switch (position) {
		case 0:
			return new BusinessBookingsFragment();
		case 1:
			return new BusinessCustomersListFragment();
		case 2:
			return new BusinessOffersFragment();
		default:
			Log.e(TAG, "trying to instantiate an unknown fragment");
			return null;
		}
	}

	@Override
	public int getMenuId() {
		return R.menu.business;
	}
	
	@Override
	public Class<? extends LoginMainActivity> getLoginMainActivity()
	{
		return BusinessLoginMainActivity.class;
	}
}