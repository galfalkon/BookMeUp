package com.gling.bookmeup.business;

import org.joda.time.DateTime;

import com.gling.bookmeup.R;
import com.gling.bookmeup.R.id;
import com.gling.bookmeup.R.layout;
import com.gling.bookmeup.R.menu;
import com.gling.bookmeup.R.string;
import com.google.gson.Gson;
import com.parse.ParseInstallation;

import android.app.Activity;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BusinessCalendarActivity extends FragmentActivity {

    private static final String TAG = "BusinessCalendarActivity";
    
    private static final int DAYS_MARGIN = 30;
    
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        Log.i(TAG, "onCreate");
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.business_calendar_activity);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.business_calendar_activity_pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setCurrentItem(DAYS_MARGIN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.business_calendar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.business_calendar_action_today) {
            mViewPager.setCurrentItem(DAYS_MARGIN);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        private DateTime dt = new DateTime();
        
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return BusinessCalendarFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return DAYS_MARGIN*2;
        }
    }

    public static class BusinessCalendarFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section number.
         */
        public static BusinessCalendarFragment newInstance(int sectionNumber) {
            BusinessCalendarFragment fragment = new BusinessCalendarFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public BusinessCalendarFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.business_calendar_fragment, container, false);
            ((TextView)rootView.findViewById(R.id.section_label)).setText(String.valueOf(getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

}
