package com.gling.bookmeup.customer;

import java.util.Locale;

import android.app.ActionBar;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gling.bookmeup.R;
import com.gling.bookmeup.login.LoginMainActivity;
import com.gling.bookmeup.main.ParseHelper;
import com.gling.bookmeup.main.PushUtils;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class CustomerMainActivity extends FragmentActivity implements ActionBar.TabListener {

    private static final String TAG = "CustomerMainActivity";

    private static final int NUM_OF_SECTIONS = 4;

    private Customer _customer;
    
    public Customer getCustomer() {
    	return _customer;
    }
    
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
     * derivative, which will keep every loaded fragment in memory. If this
     * becomes too memory intensive, it may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_main_activity);
        
        final TabListener tabListener = this;
        ParseHelper.fetchCustomer(new GetCallback<Customer>() {
			@Override
			public void done(Customer customer, ParseException e) {
				if (e != null) {
					Log.e(TAG, "Exception: " + e.getMessage());
					ParseUser.logOut();
                    Intent intent = new Intent(getApplicationContext(), LoginMainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
				}
				
				_customer = customer;
				
				// Set up the action bar.
		        final ActionBar actionBar = getActionBar();
		        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		        // Create the adapter that will return a fragment for each of the three
		        // primary sections of the activity.
		        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		        // Set up the ViewPager with the sections adapter.
		        mViewPager = (ViewPager) findViewById(R.id.customer_main_activity_pager);
		        mViewPager.setAdapter(mSectionsPagerAdapter);

		        // When swiping between different sections, select the corresponding
		        // tab. We can also use ActionBar.Tab#select() to do this if we have
		        // a reference to the Tab.
		        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
		            @Override
		            public void onPageSelected(int position) {
		                actionBar.setSelectedNavigationItem(position);
		            }
		        });

		        // For each of the sections in the app, add a tab to the action bar.
		        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
		            // Create a tab with text corresponding to the page title defined by
		            // the adapter. Also specify this Activity object, which implements
		            // the TabListener interface, as the callback (listener) for when
		            // this tab is selected.
		            actionBar.addTab(actionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i)).setTabListener(tabListener));
		        }
		        
		        PushUtils.PushNotificationType pushType = PushUtils.PushNotificationType.getFromIntent(getIntent());
        		if (pushType != null)
        		{
	        		Log.i(TAG, pushType.toString());
	        		switch (pushType)
	        		{
	        		case MESSAGE_FROM_BUSINESS:
	        			Toast.makeText(getApplicationContext(), "Not implemented", Toast.LENGTH_SHORT).show();
	        			break;
	        		case OFFER_FROM_BUSINESS:
	        			Toast.makeText(getApplicationContext(), "Not implemented", Toast.LENGTH_SHORT).show();
	        			break;
	    			default:
	    				Log.e(TAG, "Invalid push type");
	        		}
        		}
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.customer, menu);
        return true;
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
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
            case 0:
                return new CustomerPopularFragment();
            case 1:
            	return new CustomerHistoryFragment();
            case 2:
            	return new CustomerFavouriteFragment();
            case 3:
            	return new CustomerOffersFragment();
            default:
                return PlaceholderFragment.newInstance(position + 1);
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return NUM_OF_SECTIONS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
            case 0:
                return getString(R.string.customer_activity_title_section_popular).toUpperCase(l);
            case 1:
                return getString(R.string.customer_activity_title_section_history).toUpperCase(l);
            case 2:
                return getString(R.string.customer_activity_title_section_favoirites).toUpperCase(l);
            case 3:
            	return getString(R.string.customer_activity_title_section_offers).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.customer_placeholder_fragment, container, false);
            return rootView;
        }
    }

}
