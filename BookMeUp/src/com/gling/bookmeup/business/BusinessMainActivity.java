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

//public class BusinessMainActivity extends Activity implements ActionBar.TabListener {
//
//    private static final String TAG = "BusinessMainActivity";
//    private static final int NUM_OF_SECTIONS = 3;
//    
//    // TODO make static?
//    private Business _business;
//    
//    /**
//     * The {@link android.support.v4.view.PagerAdapter} that will provide
//     * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
//     * derivative, which will keep every loaded fragment in memory. If this
//     * becomes too memory intensive, it may be best to switch to a
//     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
//     */
//    SectionsPagerAdapter mSectionsPagerAdapter;
//
//    /**
//     * The {@link ViewPager} that will host the section contents.
//     */
//    ViewPager mViewPager;
//    
//    
//    public Business getBusiness() {
//        return _business;
//    }
//    
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        Log.i(TAG, "onCreate");
//        
//        final TabListener tabListener = this;
//        
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.business_main_activity);
//
//        final ProgressDialog progressDialog = ProgressDialog.show(this, null, "Loading Business..."); // TODO not showing. probably because no fragment is in container
//        
//        ParseHelper.fetchBusiness( new GetCallback<Business>() {
//
//            @Override
//            public void done(Business business, ParseException e) {
//                if (e == null) {
//                    _business = business;
//                    
//                    // Set up the action bar.
//                    final ActionBar actionBar = getActionBar();
//                    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//                    actionBar.setTitle(_business.getName());
//
//                    // Create the adapter that will return a fragment for each of the three
//                    // primary sections of the activity.
//                    mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
//
//                    // Set up the ViewPager with the sections adapter.
//                    mViewPager = (ViewPager) findViewById(R.id.business_main_activity_pager);
//                    mViewPager.setAdapter(mSectionsPagerAdapter);
//
//                    // When swiping between different sections, select the corresponding
//                    // tab. We can also use ActionBar.Tab#select() to do this if we have
//                    // a reference to the Tab.
//                    mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
//                        @Override
//                        public void onPageSelected(int position) {
//                            actionBar.setSelectedNavigationItem(position);
//                        }
//                    });
//
//                    // For each of the sections in the app, add a tab to the action bar.
//                    for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
//                        // Create a tab with text corresponding to the page title defined by
//                        // the adapter. Also specify this Activity object, which implements
//                        // the TabListener interface, as the callback (listener) for when
//                        // this tab is selected.
//                        actionBar.addTab(actionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i)).setTabListener(tabListener));
//                    }
//                    
//                    progressDialog.dismiss();
//                    
//            		PushUtils.PushNotificationType pushType = PushUtils.PushNotificationType.getFromIntent(getIntent());
//            		if (pushType != null)
//            		{
//            			Log.i(TAG, pushType.toString());
//            			switch (pushType)
//            			{
//            			case NEW_BOOKING_REQUEST:
//            				mViewPager.setCurrentItem(0);
//            				break;
//            			default:
//            				Log.e(TAG, "Invalid push type");
//            			}
//            		}
//                } else {
//                    Log.e(TAG, "Exception: " + e.getMessage());
//                    progressDialog.dismiss();
//                    ParseUser.logOut();
//                    Intent intent = new Intent(getApplicationContext(), LoginMainActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
//                }
//            }
//        });
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.business, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        Intent intent;
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        switch (item.getItemId()) {
//        case R.id.business_action_calendar:
//            intent = new Intent(this, BusinessCalendarActivity.class);
//            startActivity(intent);
//            return true;
//        case R.id.business_action_edit_profile:
//            intent = new Intent(this, BusinessProfileActivity.class);
//            startActivity(intent);
//            return true;
//        case R.id.business_action_settings:
//            return true;
//        case R.id.business_action_logout:
//            // TODO extract to session manager class
//            ParseUser.logOut();
//            intent = new Intent(this, LoginMainActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
//            return true;
//        default:
//            return super.onOptionsItemSelected(item);
//        }
//    }
//
//    @Override
//    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
//        // When the given tab is selected, switch to the corresponding page in
//        // the ViewPager.
//        mViewPager.setCurrentItem(tab.getPosition());
//    }
//
//    @Override
//    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
//    }
//
//    @Override
//    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
//    }
//
//    /**
//     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
//     * one of the sections/tabs/pages.
//     */
//    public class SectionsPagerAdapter extends FragmentPagerAdapter {
//
//        public SectionsPagerAdapter(FragmentManager fm) {
//            super(fm);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            switch (position) {
//            case 0:
//                return new BusinessBookingsFragment();
//            case 1:
//                return new BusinessCustomersListFragment();
//            case 2:
//            	return new BusinessOffersFragment();
//            default:
//                Log.e(TAG, "trying to instantiate an unknown fragment");
//                return null;
//            }
//        }
//
//        @Override
//        public int getCount() {
//            return NUM_OF_SECTIONS;
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            Locale l = Locale.getDefault();
//            switch (position) {
//            case 0:
//            	return getString(R.string.business_activity_title_section_bookings).toUpperCase(l);
//            case 1:
//                return getString(R.string.business_activity_title_section_client_list).toUpperCase(l);
//            case 2:
//            	return getString(R.string.business_activity_title_section_offer_list).toUpperCase(l);
//            }
//            return null;
//        }
//    }
//
//}

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