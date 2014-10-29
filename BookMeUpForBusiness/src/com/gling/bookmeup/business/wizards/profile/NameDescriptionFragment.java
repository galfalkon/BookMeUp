package com.gling.bookmeup.business.wizards.profile;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.gling.bookmeup.sharedlib.R;
import com.gling.bookmeup.sharedlib.parse.Business;
import com.tech.freak.wizardpager.ui.PageFragmentCallbacks;

public class NameDescriptionFragment extends Fragment {
    private static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private NameDescriptionPage mPage;
    private TextView mNameView;
    private TextView mDescriptionView;

    public static NameDescriptionFragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        NameDescriptionFragment fragment = new NameDescriptionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public NameDescriptionFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = (NameDescriptionPage) mCallbacks.onGetPage(mKey);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater
                                .inflate(R.layout.business_profile_wizard_name_description_fragment,
                                         container,
                                         false);
        ((TextView) rootView.findViewById(android.R.id.title)).setText(mPage.getTitle());

        mNameView = ((TextView) rootView.findViewById(R.id.business_profile_wizard_name));
        mDescriptionView = ((TextView) rootView
                                               .findViewById(R.id.business_profile_wizard_description));

        String name = mPage.getData().getString(NameDescriptionPage.NAME_DATA_KEY);
        if (!TextUtils.isEmpty(name)) {
            mNameView.setText(name);
        } else {
            name = Business.getCurrentBusiness().getName();
            mPage.getData().putString(NameDescriptionPage.NAME_DATA_KEY, name);
            mPage.notifyDataChanged();
            mNameView.setText(name);
        }

        String description = mPage.getData().getString(NameDescriptionPage.DESCRIPTION_DATA_KEY);
        if (!TextUtils.isEmpty(description)) {
            mDescriptionView.setText(description);
        } else {
            description = Business.getCurrentBusiness().getDescription();
            mPage.getData().putString(NameDescriptionPage.DESCRIPTION_DATA_KEY, description);
            mPage.notifyDataChanged();
            mDescriptionView.setText(description);
        }

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
                mPage.getData().putString(NameDescriptionPage.NAME_DATA_KEY,
                                          (editable != null) ? editable.toString() : null);
                mPage.notifyDataChanged();
            }
        });

        mDescriptionView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mPage.getData().putString(NameDescriptionPage.DESCRIPTION_DATA_KEY,
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
