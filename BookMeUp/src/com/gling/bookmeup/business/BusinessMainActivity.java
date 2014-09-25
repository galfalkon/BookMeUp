package com.gling.bookmeup.business;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.gling.bookmeup.R;
import com.gling.bookmeup.login.LoginMainActivity;
import com.gling.bookmeup.main.NavigationDrawerActivity;
import com.gling.bookmeup.main.ParseHelper;
import com.gling.bookmeup.main.PushUtils;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class BusinessMainActivity extends NavigationDrawerActivity {
	private static final String TAG = "BusinessMainActivity";
	
	private Business _business;
	
	public Business getBusiness() {
		return _business;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		
		final ProgressDialog progressDialog = ProgressDialog.show(this, null, "Loading Business...");
		ParseHelper.fetchBusiness( new GetCallback<Business>() {
			@Override
			public void done(Business business, ParseException e) {
				if (e != null) {
					Log.e(TAG, "Exception: " + e.getMessage());
					progressDialog.dismiss();
					ParseUser.logOut();
					Intent intent = new Intent(getApplicationContext(), LoginMainActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
					startActivity(intent);
				}
		        
		        _business = business;
		        progressDialog.dismiss();
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
		});
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.business_action_calendar:
			intent = new Intent(this, BusinessCalendarActivity.class);
			startActivity(intent);
			return true;
		case R.id.business_action_edit_profile:
			intent = new Intent(this, BusinessProfileActivity.class);
			startActivity(intent);
			return true;
		case R.id.business_action_settings:
			return true;
		case R.id.business_action_logout:
			// TODO extract to session manager class
			ParseUser.logOut();
			intent = new Intent(this, LoginMainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
}