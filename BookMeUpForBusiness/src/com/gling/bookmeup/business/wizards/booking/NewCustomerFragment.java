package com.gling.bookmeup.business.wizards.booking;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.gling.bookmeup.sharedlib.R;
import com.tech.freak.wizardpager.ui.PageFragmentCallbacks;

public class NewCustomerFragment extends Fragment {
    private static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private NewCustomerPage mPage;
    private TextView mNameView;
    private TextView mPhoneNumberView;

    public static NewCustomerFragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        NewCustomerFragment fragment = new NewCustomerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public NewCustomerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = (NewCustomerPage) mCallbacks.onGetPage(mKey);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater
                                .inflate(R.layout.business_add_booking_wizard_new_customer_fragment,
                                         container,
                                         false);
        ((TextView) rootView.findViewById(android.R.id.title)).setText(mPage.getTitle());

        mNameView = ((TextView) rootView
                                        .findViewById(R.id.business_add_booking_wizard_customer_name));
        mPhoneNumberView = ((TextView) rootView
                                               .findViewById(R.id.business_add_booking_wizard_customer_phone_number));

        mNameView.setText(mPage.getData().getString(NewCustomerPage.CUSTOMER_NAME));
        mPhoneNumberView.setText(mPage.getData().getString(NewCustomerPage.CUSTOMER_PHONE_NUMBER));

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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mNameView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mPage.getData().putString(NewCustomerPage.CUSTOMER_NAME,
                                          (editable != null) ? editable.toString() : null);
                mPage.notifyDataChanged();
            }
        });

        mPhoneNumberView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mPage.getData().putString(NewCustomerPage.CUSTOMER_PHONE_NUMBER,
                                          (editable != null) ? editable.toString() : null);
                mPage.notifyDataChanged();
            }
        });
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);

        // In a future update to the support library, this should override
        // setUserVisibleHint
        // instead of setMenuVisibility.
        if (mNameView != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (!menuVisible) {
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        }
    }
}
