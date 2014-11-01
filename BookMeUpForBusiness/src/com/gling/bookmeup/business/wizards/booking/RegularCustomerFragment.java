package com.gling.bookmeup.business.wizards.booking;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
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
import android.widget.ListView;
import android.widget.TextView;

import com.gling.bookmeup.business.R;
import com.gling.bookmeup.main.views.BaseListViewWrapperView.DisplayMode;
import com.gling.bookmeup.main.views.ListViewWrapperView;
import com.gling.bookmeup.sharedlib.parse.Business;
import com.gling.bookmeup.sharedlib.parse.Customer;
import com.gling.bookmeup.sharedlib.parse.ParseHelper.Booking;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.tech.freak.wizardpager.ui.PageFragmentCallbacks;

public class RegularCustomerFragment extends Fragment {
    private static final String TAG = "RegularCustomerFragment";
    private static final String ARG_KEY = "key";

    private PageFragmentCallbacks _callbacks;
    private String _key;
    private RegularCustomerPage _page;

    private EditText _edtCustomerName;
    private ListViewWrapperView _lstCustomers;
    private CustomersAdapter _customersAdapter;
    private List<Customer> _allCustomers;
    private List<Customer> _filteredCustomers;

    public static RegularCustomerFragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        RegularCustomerFragment fragment = new RegularCustomerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public RegularCustomerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        _key = args.getString(ARG_KEY);
        _page = (RegularCustomerPage) _callbacks.onGetPage(_key);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.business_add_booking_wizard_regular_customer_fragment,
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
                                                                               .whereEqualTo(Booking.Keys.STATUS,
                                                                                             Booking.Status.APPROVED);
        query.include(Booking.Keys.CUSTOMER_POINTER);
        query.include(Booking.Keys.SERVICE_POINTER);

        _lstCustomers.setDisplayMode(DisplayMode.LOADING_VIEW);
        _allCustomers = new ArrayList<Customer>();
        _filteredCustomers = new ArrayList<Customer>();
        query.findInBackground(new FindCallback<Booking>() {
            @Override
            public void done(List<Booking> bookings, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Exception: " + e.getMessage());
                    return;
                }

                for (Booking booking : bookings) {
                    if (!_allCustomers.contains(booking.getCustomer())) {
                        _allCustomers.add(booking.getCustomer());
                    }
                }
                _filteredCustomers.addAll(_allCustomers);

                if (isAdded()) {
                    _customersAdapter = new CustomersAdapter(getActivity(),
                            android.R.layout.simple_list_item_single_choice, android.R.id.text1,
                            _filteredCustomers);
                    _lstCustomers.setAdapter(_customersAdapter);
                    _lstCustomers.getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

                    DisplayMode newDisplayMode = bookings.isEmpty() ? DisplayMode.NO_ITEMS_VIEW
                            : DisplayMode.LIST_VIEW;
                    _lstCustomers.setDisplayMode(newDisplayMode);

                    // Pre-select currently selected item.
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            String selection = _page.getData()
                                                    .getString(RegularCustomerPage.CUSTOMER_ID);
                            if (selection != null) {
                                for (int i = 0; i < _filteredCustomers.size(); i++) {
                                    if (_filteredCustomers.get(i).getObjectId().equals(selection)) {
                                        _lstCustomers.getListView().setItemChecked(i, true);
                                        break;
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });

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
                _page.getData().putString(RegularCustomerPage.CUSTOMER_ID, null);
                _lstCustomers.getListView().setItemChecked(_lstCustomers.getListView().getCheckedItemPosition(), false);
                _page.notifyDataChanged();
                _customersAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        _lstCustomers.getListView().setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Customer customer = ((Customer) _lstCustomers.getListView().getItemAtPosition(position));
                _page.getData().putString(RegularCustomerPage.CUSTOMER_NAME, customer.getName());
                _page.getData().putString(RegularCustomerPage.CUSTOMER_ID, customer.getObjectId());
                _page.notifyDataChanged();
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

    private class CustomersAdapter extends ArrayAdapter<Customer> {

        private CustomersFilter _customersFilter;

        public CustomersAdapter(Context context, int resource, int textViewResourceId,
                List<Customer> objects) {
            super(context, resource, textViewResourceId, objects);
            _customersFilter = new CustomersFilter();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            Customer customer = getItem(position);
            if (customer != null) {
                textView.setText(customer.getName());
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

                for (Customer customer : _allCustomers) {
                    if (customer.getName()
                                .toLowerCase()
                                .contains(constraint.toString().toLowerCase()))
                        _filteredCustomers.add(customer);
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
