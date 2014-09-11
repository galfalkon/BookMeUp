package com.gling.bookmeup.business;

import org.joda.time.DateTime;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;

import com.gling.bookmeup.R;

public class BusinessCalendarActivity extends FragmentActivity {

    private static final String TAG = "BusinessCalendarActivity";
    
    private static final int DAYS_MARGIN = 30;
    
    DateTime _today;
    CalendarPagerAdapter _sectionsPagerAdapter;
    ViewPager _viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        Log.i(TAG, "onCreate");
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.business_calendar_activity);

        DateTime now = new DateTime();
        _today = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), 0, 0);
        
        // this adapter will generate a new BusinessDayViewFragment for each day.
        // it will allow scrolling for up to DAYS_MARGIN days before the anchor date,
        // and up to DAYS_MARGIN days after the anchor date
        _sectionsPagerAdapter = new CalendarPagerAdapter(getSupportFragmentManager(), _today);

        // Set up the ViewPager with the sections adapter.
        _viewPager = (ViewPager) findViewById(R.id.business_calendar_activity_pager);
        _viewPager.setAdapter(_sectionsPagerAdapter);

        // set pager to current date
        _viewPager.setCurrentItem(DAYS_MARGIN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.business_calendar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.business_calendar_action_today:
            if (!_sectionsPagerAdapter.getAnchor().equals(_today)) {
                _sectionsPagerAdapter = new CalendarPagerAdapter(getSupportFragmentManager(), _today);
                _viewPager.setAdapter(_sectionsPagerAdapter);
            }
            _viewPager.setCurrentItem(DAYS_MARGIN);
            return true;
        case R.id.business_calendar_action_pick:
            handleDatePicker();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    private void handleDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                DateTime selectedDate = new DateTime(year, monthOfYear + 1, dayOfMonth, 0, 0);
                Log.i(TAG, "Date: " + selectedDate + " selected");
                if (!_sectionsPagerAdapter.getAnchor().equals(selectedDate)) {
                    _sectionsPagerAdapter = new CalendarPagerAdapter(getSupportFragmentManager(), selectedDate);
                    _viewPager.setAdapter(_sectionsPagerAdapter);
                }
                _viewPager.setCurrentItem(DAYS_MARGIN);
            }
        }, _today.getYear(), _today.getMonthOfYear() - 1, _today.getDayOfMonth());
        datePickerDialog.show();
    }

    public class CalendarPagerAdapter extends FragmentStatePagerAdapter {

        private DateTime _anchorDate;
        
        public CalendarPagerAdapter(FragmentManager fm, DateTime anchorDate) {
            super(fm);
            _anchorDate = anchorDate;
        }
        
        public DateTime getAnchor() {
            return _anchorDate;
        }

        @Override
        public Fragment getItem(int position) {
            return BusinessCalendarFragment.newInstance(_anchorDate.minusDays(DAYS_MARGIN).plusDays(position));
        }

        @Override
        public int getCount() {
            return DAYS_MARGIN*2;
        }
    }

}
