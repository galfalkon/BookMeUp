package com.gling.bookmeup.customer;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.gling.bookmeup.R;
import com.gling.bookmeup.login.LoginMainActivity;
import com.gling.bookmeup.main.NavigationDrawerActivity;
import com.gling.bookmeup.main.PushUtils;
import com.parse.ParseUser;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class CustomerMainActivity extends NavigationDrawerActivity {
	private static final String TAG = "CustomerMainActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);

		if (Customer.getCurrentCustomer() == null)
		{
			Log.e(TAG, "No current customer");
			
			ParseUser.logOut();
			Intent intent = new Intent(getApplicationContext(),
					LoginMainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
			return;
		}
		
		PushUtils.PushNotificationType pushType = PushUtils.PushNotificationType
				.getFromIntent(getIntent());
		if (pushType != null) {
			Log.i(TAG, String.format("pushType = %s", pushType.toString()));
			
			switch (pushType) {
			case MESSAGE_FROM_BUSINESS:
				Crouton.showText(this, "Not implemented", Style.ALERT);
				break;
			case OFFER_FROM_BUSINESS:
				Crouton.showText(this, "Not implemented", Style.ALERT);
				break;
			case BOOKING_APPROVED:
			case BOOKING_CANCELED:
				onNavigationDrawerItemSelected(3);
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
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.customer_action_settings:
			return true;
		case R.id.customer_action_logout:
			// TODO extract to session manager class
			ParseUser.logOut();
			Intent intent = new Intent(this, LoginMainActivity.class);
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
		return getResources().getStringArray(R.array.customer_navigation_drawer_items);
	}

	@Override
	protected Fragment getSectionFragment(int position) {
		switch (position) {
		case 0:
			return new CustomerAllBusinessesFragment();
		case 1:
			return new CustomerHistoryFragment();
		case 2:
			return new CustomerFavouriteFragment();
		case 3:
			return new CustomerMyBookingsFragment();
		case 4:
			return new CustomerOffersFragment();
		default:
			Log.e(TAG, "trying to instantiate an unknown fragment");
			return null;
		}
	}

	@Override
	public int getMenuId() {
		return R.menu.customer;
	}
}
