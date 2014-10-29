package com.gling.bookmeup.business.wizards.booking;

import org.joda.time.DateTime;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.gling.bookmeup.business.R;
import com.tech.freak.wizardpager.model.Page;
import com.tech.freak.wizardpager.ui.PageFragmentCallbacks;

public class DateFragment extends Fragment {
    private static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private DatePage mPage;
    private DatePicker mDateView;
    private TimePicker mTimeView;

    public static DateFragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        DateFragment fragment = new DateFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public DateFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = (DatePage) mCallbacks.onGetPage(mKey);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.business_add_booking_wizard_date_fragment,
                                         container,
                                         false);
        ((TextView) rootView.findViewById(android.R.id.title)).setText(mPage.getTitle());

        mDateView = ((DatePicker) rootView.findViewById(R.id.business_add_booking_wizard_date));
        // TimePicker time default is current time
        mTimeView = ((TimePicker) rootView.findViewById(R.id.business_add_booking_wizard_time));

        DateTime date = (DateTime) mPage.getData().getSerializable(Page.SIMPLE_DATA_KEY);
        if (date != null) {
            mDateView.updateDate(date.getYear(), date.getMonthOfYear() - 1, date.getDayOfMonth());
            mTimeView.setCurrentHour(date.getHourOfDay());
            mTimeView.setCurrentMinute(date.getMinuteOfDay());
        }

        mDateView.init(mDateView.getYear(),
                       mDateView.getMonth(),
                       mDateView.getDayOfMonth(),
                       new OnDateChangedListener() {

                           @Override
                           public void onDateChanged(DatePicker paramDatePicker, int year,
                                   int monthOfYear, int dayOfMonth) {
                               DateTime date = new DateTime(year, monthOfYear + 1, dayOfMonth,
                                       mTimeView.getCurrentHour(), mTimeView.getCurrentMinute());
                               mPage.getData().putSerializable(DatePage.SIMPLE_DATA_KEY, date);
                               mPage.notifyDataChanged();
                           }
                       });

        //mTimeView.setIs24HourView(true);
        mTimeView.setOnTimeChangedListener(new OnTimeChangedListener() {

            @Override
            public void onTimeChanged(TimePicker paramTimePicker, int hour, int minute) {
                DateTime date = new DateTime(mDateView.getYear(), mDateView.getMonth() + 1,
                        mDateView.getDayOfMonth(), hour, minute);
                mPage.getData().putSerializable(DatePage.SIMPLE_DATA_KEY, date);
                mPage.notifyDataChanged();
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof PageFragmentCallbacks)) {
            throw new ClassCastException("Activity must implement PageFragmentCallbacks");
        }

        mCallbacks = (PageFragmentCallbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);

        // In a future update to the support library, this should override
        // setUserVisibleHint
        // instead of setMenuVisibility.
        if (mDateView != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (!menuVisible) {
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        }
    }
}
