package com.gling.bookmeup.business.wizards.booking;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.TextView;

import com.gling.bookmeup.business.R;
import com.gling.bookmeup.main.views.BaseListViewWrapperView.DisplayMode;
import com.gling.bookmeup.main.views.ListViewWrapperView;
import com.gling.bookmeup.sharedlib.parse.Business;
import com.gling.bookmeup.sharedlib.parse.ParseHelper.Booking;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.tech.freak.wizardpager.ui.PageFragmentCallbacks;

public class CustomerFragment extends Fragment {
    private static final String TAG = "CustomerFragment";
    private static final String ARG_KEY = "key";

    private PageFragmentCallbacks _callbacks;
    private String _key;
    private CustomerPage _page;

    private EditText _edtCustomerName;
    private ListViewWrapperView _lstCustomers;
    private CustomersAdapter _customersAdapter;
    private List<Booking> _allCustomers;
    private List<Booking> _filteredCustomers;

    public static CustomerFragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        CustomerFragment fragment = new CustomerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public CustomerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        _key = args.getString(ARG_KEY);
        _page = (CustomerPage) _callbacks.onGetPage(_key);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.business_add_booking_wizard_customer_fragment,
                                     container,
                                     false);
        ((TextView) view.findViewById(android.R.id.title)).setText(_page.getTitle());

        _lstCustomers = (ListViewWrapperView) view
                                                  .findViewById(R.id.business_add_booking_wizard_customers_list);
        _edtCustomerName = (EditText) view
                                          .findViewById(R.id.business_add_booking_wizard_customer_name);

        ParseQuery<Booking> query = new ParseQuery<Booking>(Booking.CLASS_NAME).whereEqualTo(Booking.Keys.BUSINESS_POINTER,
                                                                                             Business
                                                                                                     .getCurrentBusiness())
                                                                               .whereLessThan(Booking.Keys.DATE,
                                                                                              new Date())
                                                                               .whereEqualTo(Booking.Keys.STATUS,
                                                                                             Booking.Status.APPROVED);
        query.include(Booking.Keys.CUSTOMER_POINTER);
        query.include(Booking.Keys.SERVICE_POINTER);

        _lstCustomers.setDisplayMode(DisplayMode.LOADING_VIEW);
        _allCustomers = new ArrayList<Booking>();
        _filteredCustomers = new ArrayList<Booking>();
        query.findInBackground(new FindCallback<Booking>() {
            @Override
            public void done(List<Booking> bookings, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Exception: " + e.getMessage());
                    return;
                }

                if (isAdded()) {
                    _allCustomers = bookings;
                    _filteredCustomers.addAll(bookings);
                    _customersAdapter = new CustomersAdapter(getActivity(),
                                                             android.R.layout.simple_list_item_1, _filteredCustomers);
                    _lstCustomers.setAdapter(_customersAdapter);
                    
                    DisplayMode newDisplayMode = bookings.isEmpty() ? DisplayMode.NO_ITEMS_VIEW
                            : DisplayMode.LIST_VIEW;
                    _lstCustomers.setDisplayMode(newDisplayMode);
                }
            }
        });

        String customerName = _page.getData().getString(CustomerPage.CUSTOMER_NAME);
        if (!TextUtils.isEmpty(customerName)) {
            _edtCustomerName.setText(customerName);
        }

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof PageFragmentCallbacks)) {
            throw new ClassCastException("Activity must implement PageFragmentCallbacks");
        }

        _callbacks = (PageFragmentCallbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        _callbacks = null;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        _edtCustomerName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                _customersAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                _page.getData().putString(CustomerPage.CUSTOMER_NAME,
                                          (editable != null) ? editable.toString() : null);
                _page.notifyDataChanged();
            }
        });

        _lstCustomers.getListView().setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Booking booking = (Booking) _lstCustomers.getListView().getItemAtPosition(position);
                _edtCustomerName.setText(booking.getCustomer().getName());
            }
        });
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);

        // In a future update to the support library, this should override
        // setUserVisibleHint
        // instead of setMenuVisibility.
        if (_edtCustomerName != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (!menuVisible) {
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        }
    }

    private class CustomersAdapter extends ArrayAdapter<Booking> {

        private CustomersFilter _customersFilter;

        public CustomersAdapter(Context context, int resource, List<Booking> objects) {
            super(context, resource, objects);
            _customersFilter = new CustomersFilter();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            Booking booking = getItem(position);
            if (booking != null) {
                textView.setText(booking.getCustomer().getName());
            }
            return view;
        }

        @Override
        public Filter getFilter() {
            return _customersFilter;
        }
    }

    private class CustomersFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            _filteredCustomers.clear();
            if (constraint == null || constraint.length() == 0) {
                _filteredCustomers.addAll(_allCustomers);
            } else {

                for (Booking booking : _allCustomers) {
                    if (booking.getCustomer()
                               .getName()
                               .toLowerCase()
                               .contains(constraint.toString().toLowerCase()))
                        _filteredCustomers.add(booking);
                }
            }
            results.values = _filteredCustomers;
            results.count = _filteredCustomers.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.count == 0)
                _customersAdapter.notifyDataSetInvalidated();
            else {
                _customersAdapter.notifyDataSetChanged();
            }
        }

    }
}
