package com.gling.bookmeup.business;

import org.joda.time.DateTime;

import com.gling.bookmeup.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BusinessCalendarActivity extends FragmentActivity {

    private static final String TAG = "BusinessCalendarActivity";
    
    private static final int DAYS_MARGIN = 30;
    
    DayViewPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        Log.i(TAG, "onCreate");
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.business_calendar_activity);

        // this adapter will generate a new BusinessCalendarDayViewFragment for each day.
        // it will allow scrolling for up to DAYS_MARGIN days before current time,
        // and up to DAYS_MARGIN days after current time
        mSectionsPagerAdapter = new DayViewPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.business_calendar_activity_pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // set pager to current date
        mViewPager.setCurrentItem(DAYS_MARGIN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.business_calendar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.business_calendar_action_today) {
            mViewPager.setCurrentItem(DAYS_MARGIN);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class DayViewPagerAdapter extends FragmentStatePagerAdapter {

        public DayViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return BusinessCalendarDayViewFragment.newInstance(DateTime.now().minusDays(DAYS_MARGIN).plusDays(position));
        }

        @Override
        public int getCount() {
            return DAYS_MARGIN*2;
        }
    }

    public static class BusinessCalendarDayViewFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_DATE = "section_date";

        /**
         * Returns a new instance of this fragment for the given section date.
         */
        public static BusinessCalendarDayViewFragment newInstance(DateTime dateTime) {
            BusinessCalendarDayViewFragment fragment = new BusinessCalendarDayViewFragment();
            Bundle args = new Bundle();
            args.putSerializable(ARG_DATE, dateTime);
            fragment.setArguments(args);
            return fragment;
        }
        
        public BusinessCalendarDayViewFragment() {
            
        }
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.business_calendar_fragment, container, false);
            ((TextView)rootView.findViewById(R.id.section_label)).setText(getArguments().getSerializable(ARG_DATE).toString());
            return rootView;
        }
    }

}
