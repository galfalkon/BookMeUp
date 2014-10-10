package com.gling.bookmeup.customer;

import java.util.List;

import org.joda.time.DateTime;

import android.content.Context;
import android.os.Bundle;
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

public class CustomerCalendarFragment extends Fragment {

    private static final String TAG = "CustomerCalendarFragment";
    private static final String ARG_DATE = "customer_calendar_fragment_date";

    private DateTime _date;
    private CustomParseQueryAdapter _customerBookingsAdapter;
    
    private ListView _lstBookings;
    private ProgressBar _progressBar;
    
    /**
     * Returns a new instance of this fragment for the given date.
     */
    public static CustomerCalendarFragment newInstance(DateTime dateTime) {
        CustomerCalendarFragment fragment = new CustomerCalendarFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, dateTime);
        fragment.setArguments(args);
        return fragment;
    }

    public CustomerCalendarFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        
        _date = (DateTime) getArguments().getSerializable(ARG_DATE);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.customer_calendar_fragment, container, false);
        
        _lstBookings = (ListView) rootView.findViewById(R.id.customer_calendar_list);
        _progressBar = (ProgressBar) rootView.findViewById(R.id.customer_calendar_progress_bar);        
        
        _customerBookingsAdapter = new CustomParseQueryAdapter(getActivity());      
        _lstBookings.setAdapter(_customerBookingsAdapter);
        
        _customerBookingsAdapter.addOnQueryLoadListener(new OnQueryLoadListener<Booking>() {
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
                    query.whereEqualTo(Booking.Keys.CUSTOMER_POINTER, ParseUser.getCurrentUser().get(ParseHelper.User.Keys.CUSTOMER_POINTER));
                    query.whereGreaterThanOrEqualTo(Booking.Keys.DATE, _date.toDate());
                    query.whereLessThan(Booking.Keys.DATE, _date.plusDays(1).toDate());
                    query.include(Booking.Keys.BUSINESS_POINTER);
                    query.include(Booking.Keys.SERVICE_POINTER);
                    return query;
                }
            });
        }

        @Override
        public View getItemView(Booking booking, View v, ViewGroup parent) {
            if (v == null) {
                v = View.inflate(getContext(), R.layout.customer_calendar_item, null);
            }
            super.getItemView(booking, v, parent);
            
            TextView txtCustomerName = (TextView) v.findViewById(R.id.customer_calendar_business_name);
//            ParseImageView imgCustomerImage = (ParseImageView) v.findViewById(R.id.customer_calendar_business_image);
            TextView txtBookingTime = (TextView) v.findViewById(R.id.customer_calendar_booking_date);
            TextView txtBookingService = (TextView) v.findViewById(R.id.customer_calendar_booking_service);
            TextView txtBookingStatus = (TextView) v.findViewById(R.id.customer_calendar_booking_status);

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