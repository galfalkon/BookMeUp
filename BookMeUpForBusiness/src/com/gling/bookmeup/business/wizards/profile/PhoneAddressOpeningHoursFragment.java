package com.gling.bookmeup.business.wizards.profile;

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

public class PhoneAddressOpeningHoursFragment extends Fragment {
    private static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private PhoneAddressOpeningHoursPage mPage;
    private TextView mPhoneView;
    private TextView mAddressView;
    private TextView mOpeningHoursView;

    public static PhoneAddressOpeningHoursFragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        PhoneAddressOpeningHoursFragment fragment = new PhoneAddressOpeningHoursFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public PhoneAddressOpeningHoursFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = (PhoneAddressOpeningHoursPage) mCallbacks.onGetPage(mKey);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater
                                .inflate(R.layout.business_profile_wizard_phone_opening_hours_fragment,
                                         container,
                                         false);
        ((TextView) rootView.findViewById(android.R.id.title)).setText(mPage.getTitle());

        mPhoneView = ((TextView) rootView.findViewById(R.id.business_profile_wizard_phone));
        mAddressView = ((TextView) rootView.findViewById(R.id.business_profile_wizard_address));
        mOpeningHoursView = ((TextView) rootView
                                                .findViewById(R.id.business_profile_wizard_opening_hours));

        mPhoneView.setText(mPage.getData().getString(PhoneAddressOpeningHoursPage.PHONE_DATA_KEY));
        mAddressView.setText(mPage.getData()
                                  .getString(PhoneAddressOpeningHoursPage.ADDRESS_DATA_KEY));
        mOpeningHoursView
                         .setText(mPage.getData()
                                       .getString(PhoneAddressOpeningHoursPage.OPENING_HOURS_DATA_KEY));
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

        mPhoneView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mPage.getData().putString(PhoneAddressOpeningHoursPage.PHONE_DATA_KEY,
                                          (editable != null) ? editable.toString() : null);
                mPage.notifyDataChanged();
            }
        });

        mAddressView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mPage.getData().putString(PhoneAddressOpeningHoursPage.ADDRESS_DATA_KEY,
                                          (editable != null) ? editable.toString() : null);
                mPage.notifyDataChanged();
            }
        });

        mOpeningHoursView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mPage.getData().putString(PhoneAddressOpeningHoursPage.OPENING_HOURS_DATA_KEY,
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
        if (mPhoneView != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (!menuVisible) {
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        }
    }
}
