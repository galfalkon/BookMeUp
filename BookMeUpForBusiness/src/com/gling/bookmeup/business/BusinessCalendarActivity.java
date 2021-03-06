package com.gling.bookmeup.business;

import org.joda.time.DateTime;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.DatePicker;

import com.astuetz.PagerSlidingTabStrip;
import com.gling.bookmeup.business.wizards.booking.BusinessAddBookingWizardActivity;

public class BusinessCalendarActivity extends FragmentActivity {

	private static final String TAG = "BusinessCalendarActivity";

	public static String EXTRA_DATE = "com.gling.bookmeup.buiness.EXTRA_DATE";
	
	private static final int DAYS_MARGIN = 30;

	private DateTime _today;

	private PagerSlidingTabStrip _tabs;
	private ViewPager _viewPager;
	private CalendarPagerAdapter _sectionsPagerAdapter;
	private int _scrollCenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		Log.i(TAG, "onCreate");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.business_calendar_activity);

		DateTime now = new DateTime();
		_today = new DateTime(now.getYear(), now.getMonthOfYear(),
				now.getDayOfMonth(), 0, 0);

		_tabs = (PagerSlidingTabStrip) findViewById(R.id.business_calendar_activity_tabs);
		// Set up the ViewPager with the sections adapter.
		_viewPager = (ViewPager) findViewById(R.id.business_calendar_activity_pager);
		// this adapter will generate a new BusinessDayViewFragment for each
		// day.
		// it will allow scrolling for up to DAYS_MARGIN days before the anchor
		// date,
		// and up to DAYS_MARGIN days after the anchor date
		_sectionsPagerAdapter = new CalendarPagerAdapter(
				getSupportFragmentManager(), _today);
		_viewPager.setAdapter(_sectionsPagerAdapter);
		_tabs.setViewPager(_viewPager);

		// set pager to current date
		_viewPager.setCurrentItem(DAYS_MARGIN);
		
		// get the scroll offset of the central (today) tab
		_scrollCenter = 0;
		ViewTreeObserver vto = _tabs.getViewTreeObserver(); 
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
		    @SuppressWarnings("deprecation")
			@Override
		    public void onGlobalLayout() {
		    	_scrollCenter = _tabs.getScrollX();
		        ViewTreeObserver obs = _tabs.getViewTreeObserver();
		        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
		            obs.removeOnGlobalLayoutListener(this);
		        } else {
		            obs.removeGlobalOnLayoutListener(this);
		        }
		    }
		});
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
				_sectionsPagerAdapter = new CalendarPagerAdapter(
						getSupportFragmentManager(), _today);
				_viewPager.setAdapter(_sectionsPagerAdapter);
				_tabs.setViewPager(_viewPager);
			} else {
				_tabs.smoothScrollTo(_scrollCenter, 0);	
			}
			_viewPager.setCurrentItem(DAYS_MARGIN, true);
			return true;
		case R.id.business_calendar_action_pick:
			handleDatePicker();
			return true;
		case R.id.business_calendar_add_booking:
            addBooking();
            return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void addBooking() {
	    Intent intent = new Intent(this, BusinessAddBookingWizardActivity.class);
	    intent.putExtra(EXTRA_DATE, _sectionsPagerAdapter._anchorDate.plusDays(_viewPager.getCurrentItem() - DAYS_MARGIN));
	    startActivityForResult(intent, 0);
    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	    if (requestCode == 0) {
	        if (resultCode == RESULT_CANCELED) {
	            _sectionsPagerAdapter.notifyDataSetChanged();
	        }
	    }
	}

    private void handleDatePicker() {
		DatePickerDialog datePickerDialog = new DatePickerDialog(this,
				new OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						DateTime selectedDate = new DateTime(year,
								monthOfYear + 1, dayOfMonth, 0, 0);
						Log.i(TAG, "Date: " + selectedDate + " selected");
						if (!_sectionsPagerAdapter.getAnchor().equals(
								selectedDate)) {
							_sectionsPagerAdapter = new CalendarPagerAdapter(
									getSupportFragmentManager(), selectedDate);
							_viewPager.setAdapter(_sectionsPagerAdapter);
							_tabs.setViewPager(_viewPager);
						} else {
							_tabs.smoothScrollTo(_scrollCenter, 0);				
						}
						_viewPager.setCurrentItem(DAYS_MARGIN, true);
					}
				}, _today.getYear(), _today.getMonthOfYear() - 1,
				_today.getDayOfMonth());
		datePickerDialog.show();
	}

	public class CalendarPagerAdapter extends FragmentStatePagerAdapter {

		private DateTime _anchorDate;
		private final static String DATE_FORMAT = "E, dd MMM";

		private final static int COUNT = DAYS_MARGIN * 2 + 1;
		private String[] titles = new String[COUNT];

		public CalendarPagerAdapter(FragmentManager fm, DateTime anchorDate) {
			super(fm);
			_anchorDate = anchorDate;
			generateTitles();
		}

		private void generateTitles() {

			titles[DAYS_MARGIN] = _anchorDate.toString(DATE_FORMAT);
			DateTime past = new DateTime(_anchorDate);
			DateTime future = new DateTime(_anchorDate);

			for (int i = 1; i <= DAYS_MARGIN; i++) {
				past = past.plusDays(-1);
				titles[DAYS_MARGIN - i] = past.toString(DATE_FORMAT);

				future = future.plusDays(1);
				titles[DAYS_MARGIN + i] = future.toString(DATE_FORMAT);
			}

		}

		public DateTime getAnchor() {
			return _anchorDate;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return titles[position];
		}

		@Override
		public Fragment getItem(int position) {
			return BusinessCalendarFragment.newInstance(_anchorDate.minusDays(
					DAYS_MARGIN).plusDays(position));
		}

		@Override
		public int getCount() {
			return COUNT;
		}
		
		//http://stackoverflow.com/questions/7263291/viewpager-pageradapter-not-updating-the-view/7287121#7287121
		@Override
		public int getItemPosition(Object object) {
		    return POSITION_NONE;
		}
	}

}
