package com.gling.bookmeup.customer;

import org.joda.time.DateTime;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
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

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class CustomerCalendarActivity extends FragmentActivity {

	private static final String TAG = "CustomerCalendarActivity";
	
	public static final String BUSINESS_ID_EXTRA = "BusinessIdExtra";
	public static final String SERVICE_ID_EXTRA = "ServiceIdExtra";
	public static final String OFFER_DISCOUNT_EXTRA = "OfferDiscountExtra";
	public static final String OFFER_EXPIRATION_EXTRA = "OfferExpirationExtra";

	private static final int DAYS_MARGIN = 30;
	private static final int START_DAY = 0;

	private DateTime _today;

	private PagerSlidingTabStrip _tabs;
	private ViewPager _viewPager;
	private CalendarPagerAdapter _sectionsPagerAdapter;
	private int _scrollCenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		Log.i(TAG, "onCreate");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.customer_calendar_activity);

		DateTime now = new DateTime();
		_today = new DateTime(now.getYear(), now.getMonthOfYear(),
				now.getDayOfMonth(), 0, 0);

		_tabs = (PagerSlidingTabStrip) findViewById(R.id.customer_calendar_activity_tabs);
		// Set up the ViewPager with the sections adapter.
		_viewPager = (ViewPager) findViewById(R.id.customer_calendar_activity_pager);
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
		_viewPager.setCurrentItem(START_DAY);
		
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
	
	public String getStringExtra(String extraName) {
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			return extras.getString(extraName);
		}
		return null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.customer_calendar, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.customer_calendar_action_today:
			if (!_sectionsPagerAdapter.getAnchor().equals(_today)) {
				_sectionsPagerAdapter = new CalendarPagerAdapter(
						getSupportFragmentManager(), _today);
				_viewPager.setAdapter(_sectionsPagerAdapter);
				_tabs.setViewPager(_viewPager);
				_viewPager.setCurrentItem(START_DAY, true);
			} else {
				_tabs.smoothScrollTo(_scrollCenter, 0);				
			}
			return true;
		case R.id.customer_calendar_action_pick:
			handleDatePicker();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void handleDatePicker() {
		final Activity thisActivity = this;
		DatePickerDialog datePickerDialog = new DatePickerDialog(this,
				new OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						DateTime selectedDate = new DateTime(year,
								monthOfYear + 1, dayOfMonth, 0, 0);
						if (selectedDate.toDate().before(_today.toDate())) {
							thisActivity.runOnUiThread(new Runnable() {
								public void run() {
									Crouton.showText(thisActivity,
											R.string.customer_calendar_past_date_picked_text_error,
											Style.ALERT);
								}
							});
							return;
						}
						Log.i(TAG, "Date: " + selectedDate + " selected");
						if (!_sectionsPagerAdapter.getAnchor().equals(
								selectedDate)) {
							_sectionsPagerAdapter = new CalendarPagerAdapter(
									getSupportFragmentManager(), selectedDate);
							_viewPager.setAdapter(_sectionsPagerAdapter);
							_tabs.setViewPager(_viewPager);
//							_viewPager.setCurrentItem(DAYS_MARGIN, true);
							_viewPager.setCurrentItem(START_DAY, true);
						} else {
							_tabs.smoothScrollTo(_scrollCenter, 0);				
						}
					}
				}, _today.getYear(), _today.getMonthOfYear() - 1,
				_today.getDayOfMonth());
		datePickerDialog.show();
	}

	public class CalendarPagerAdapter extends FragmentStatePagerAdapter {

		private DateTime _anchorDate;
		private final static String DATE_FORMAT = "E, dd MMM";

		private final static int COUNT = DAYS_MARGIN + 1;
		private String[] titles = new String[COUNT];

		public CalendarPagerAdapter(FragmentManager fm, DateTime anchorDate) {
			super(fm);
			_anchorDate = anchorDate;
			generateTitles();
		}

		private void generateTitles() {

			titles[START_DAY] = _anchorDate.toString(DATE_FORMAT);
			DateTime future = new DateTime(_anchorDate);

			for (int i = 1; i <= DAYS_MARGIN; i++) {
				future = future.plusDays(1);
				titles[START_DAY + i] = future.toString(DATE_FORMAT);
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
			return CustomerCalendarFragment.newInstance(_anchorDate.plusDays(position));
		}

		@Override
		public int getCount() {
			return COUNT;
		}
	}

}
