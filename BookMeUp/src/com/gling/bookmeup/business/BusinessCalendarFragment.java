package com.gling.bookmeup.business;

import java.util.List;

import org.joda.time.DateTime;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Process;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gling.bookmeup.R;
import com.gling.bookmeup.main.ParseHelper;
import com.gling.bookmeup.main.ParseHelper.Booking;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseQueryAdapter.OnQueryLoadListener;
import com.parse.ParseUser;

public class BusinessCalendarFragment extends Fragment {

    private static final String TAG = "BusinessCalendarFragment";
    private static final String ARG_DATE = "business_calendar_fragment_date";

    private DateTime _date;
    private CustomParseQueryAdapter _businessBookingsAdapter;
    
    private ListView _lstBookings;
    private ProgressBar _progressBar;
    
    /**
     * Returns a new instance of this fragment for the given date.
     */
    public static BusinessCalendarFragment newInstance(DateTime dateTime) {
        BusinessCalendarFragment fragment = new BusinessCalendarFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, dateTime);
        fragment.setArguments(args);
        return fragment;
    }

    public BusinessCalendarFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        
        _date = (DateTime) getArguments().getSerializable(ARG_DATE);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.business_calendar_fragment, container, false);
        
        _lstBookings = (ListView) rootView.findViewById(R.id.business_calendar_list);
        _progressBar = (ProgressBar) rootView.findViewById(R.id.business_calendar_progress_bar);        
        
        _businessBookingsAdapter = new CustomParseQueryAdapter(getActivity());      
        _lstBookings.setAdapter(_businessBookingsAdapter);
        
        _businessBookingsAdapter.addOnQueryLoadListener(new OnQueryLoadListener<Booking>() {
            @Override
            public void onLoading() {
            	_lstBookings.setVisibility(View.VISIBLE);
            	_progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoaded(List<Booking> objects, Exception e) {
            	_progressBar.setVisibility(View.GONE);
            	_lstBookings.setVisibility(View.VISIBLE);
            }

          });

        return rootView;
    }
    
    public class CustomParseQueryAdapter extends ParseQueryAdapter<Booking> {
        
        public CustomParseQueryAdapter(Context context) {
            super(context, new ParseQueryAdapter.QueryFactory<Booking>() {
                public ParseQuery<Booking> create() {
                    ParseQuery<Booking> query = new ParseQuery<Booking>(Booking.CLASS_NAME);
                    query.whereEqualTo(Booking.Keys.BUSINESS_POINTER, ParseUser.getCurrentUser().get(ParseHelper.User.Keys.BUSINESS_POINTER));
                    query.whereGreaterThanOrEqualTo(Booking.Keys.DATE, _date.toDate());
                    query.whereLessThan(Booking.Keys.DATE, _date.plusDays(1).toDate());
                    query.include(Booking.Keys.CUSTOMER_POINTER);
                    query.include(Booking.Keys.SERVICE_POINTER);
                    return query;
                }
            });
        }

        @Override
        public View getItemView(Booking booking, View v, ViewGroup parent) {
            if (v == null) {
                v = View.inflate(getContext(), R.layout.business_calendar_item, null);
            }
            super.getItemView(booking, v, parent);
            
            TextView txtCustomerName = (TextView) v.findViewById(R.id.business_calendar_customer_name);
//            ParseImageView imgCustomerImage = (ParseImageView) v.findViewById(R.id.business_calendar_customer_image);
            TextView txtBookingTime = (TextView) v.findViewById(R.id.business_calendar_booking_date);
            TextView txtBookingService = (TextView) v.findViewById(R.id.business_calendar_booking_service);
            TextView txtBookingStatus = (TextView) v.findViewById(R.id.business_calendar_booking_status);

            // Add customer name
            txtCustomerName.setText(booking.getCustomerName());
            
            // Add and download customer image
//            ParseFile imageFile = booking.getParseFile("image");
//            if (imageFile != null) {
//                customerImage.setParseFile(imageFile);
//                customerImage.loadInBackground();
//            }

            // Add booking time
            txtBookingTime.setText(new DateTime(booking.getDate()).toString("HH:mm"));
            
            // Add booked service
            txtBookingService.setText(booking.getServiceName());
            
            // Add booking status
            txtBookingStatus.setText(String.valueOf(booking.getStatus()));
            
            return v;
        }
    }

}