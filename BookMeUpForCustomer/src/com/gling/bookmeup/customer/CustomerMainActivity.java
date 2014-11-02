package com.gling.bookmeup.customer;

import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.gling.bookmeup.customer.login.CustomerLoginMainActivity;
import com.gling.bookmeup.main.NavigationDrawerActivity;
import com.gling.bookmeup.main.PushUtils;
import com.gling.bookmeup.sharedlib.R;
import com.gling.bookmeup.sharedlib.login.LoginMainActivityBase;
import com.gling.bookmeup.sharedlib.parse.Business;
import com.gling.bookmeup.sharedlib.parse.Customer;
import com.gling.bookmeup.sharedlib.parse.ParseHelper;
import com.parse.ParseUser;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class CustomerMainActivity extends NavigationDrawerActivity {
	private static final String TAG = "CustomerMainActivity";
	
	public static final String GO_TO_BOOKING_EXTRA = "Go_To_Booking";
	
	private CustomerAllBusinessesFragment _fragment = null;
	private Business _chosenBusiness = null;
	
	public void setAllFragment(CustomerAllBusinessesFragment fragment) {
		this._fragment = fragment;
	}
	
	public void setChosenBusiness(Business business) {
		this._chosenBusiness = business;
	}
	
	public Business getChosenBusiness() {
		return this._chosenBusiness;
	}
	
	@Override
	public void onBackPressed() {
		Log.i(TAG, "onBackPressed");
		if (_fragment == null) {
			super.onBackPressed();
		} else {
			_fragment.clearAll();
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);

		if (Customer.getCurrentCustomer() == null)
		{
			Log.e(TAG, "No current customer");
			
			ParseUser.logOut();
			Intent intent = new Intent(getApplicationContext(),
					LoginMainActivityBase.class);
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
				onNavigationDrawerItemSelected(4);
				break;
			case BOOKING_APPROVED:
			case BOOKING_CANCELED:
				onNavigationDrawerItemSelected(3);
				break;
			default:
				Log.e(TAG, "Invalid push type");
			}
		}
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			Boolean goToBooking = extras.getBoolean(GO_TO_BOOKING_EXTRA);
			if (goToBooking) {
				onNavigationDrawerItemSelected(3);
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
	    Intent intent;
		switch (item.getItemId()) {
		case R.id.customer_action_send_feedback:
            intent = new Intent("android.intent.action.SENDTO");
            intent.setType("message/rfc822");
            intent.setData(Uri.parse("mailto:support@bookmeup.com"));
            intent.putExtra("android.intent.extra.SUBJECT", this.getString(R.string.feedback_subject));
            intent.putExtra("android.intent.extra.TEXT", this.getString(R.string.feedback_text));
            try {
                startActivity(Intent.createChooser(intent, this.getString(R.string.send_feedback)));
            } catch (ActivityNotFoundException localActivityNotFoundException) {
                Crouton.showText(this, R.string.no_email_clients_installed, Style.ALERT);
            }
            return true;
		case R.id.customer_action_logout:
			// TODO extract to session manager class
			ParseHelper.logOut();
			intent = new Intent(this, CustomerLoginMainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
	
	@Override
	public Class<? extends LoginMainActivityBase> getLoginMainActivity() 
	{
		return CustomerLoginMainActivity.class;
	}
}
