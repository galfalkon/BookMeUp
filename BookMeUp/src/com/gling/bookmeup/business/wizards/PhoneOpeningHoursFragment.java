package com.gling.bookmeup.business.wizards;

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

import com.gling.bookmeup.R;
import com.gling.bookmeup.business.Business;
import com.gling.bookmeup.main.ParseHelper;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.tech.freak.wizardpager.ui.PageFragmentCallbacks;

public class PhoneOpeningHoursFragment extends Fragment {
	private static final String ARG_KEY = "key";

	private PageFragmentCallbacks mCallbacks;
	private String mKey;
	private PhoneOpeningHoursPage mPage;
	private TextView mPhoneView;
	private TextView mOpeningHoursView;

	public static PhoneOpeningHoursFragment create(String key) {
		Bundle args = new Bundle();
		args.putString(ARG_KEY, key);

		PhoneOpeningHoursFragment fragment = new PhoneOpeningHoursFragment();
		fragment.setArguments(args);
		return fragment;
	}

	public PhoneOpeningHoursFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle args = getArguments();
		mKey = args.getString(ARG_KEY);
		mPage = (PhoneOpeningHoursPage) mCallbacks.onGetPage(mKey);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.business_profile_wizard_phone_opening_hours_fragment,
				container, false);
		((TextView) rootView.findViewById(android.R.id.title)).setText(mPage
				.getTitle());

		mPhoneView = ((TextView) rootView
				.findViewById(R.id.business_profile_wizard_phone));
		mOpeningHoursView = ((TextView) rootView
				.findViewById(R.id.business_profile_wizard_opening_hours));

		String phone = mPage.getData().getString(
				PhoneOpeningHoursPage.PHONE_DATA_KEY);
		if (!TextUtils.isEmpty(phone)) {
			mPhoneView.setText(phone);
		} else {
			ParseHelper.fetchBusiness(new GetCallback<Business>() {

				@Override
				public void done(Business business, ParseException e) {
					mPage.getData().putString(PhoneOpeningHoursPage.PHONE_DATA_KEY,
							business.getPhoneNumber());
					mPage.notifyDataChanged();
				}
			});
		}

		String openingHours = mPage.getData().getString(
				PhoneOpeningHoursPage.OPENING_HOURS_DATA_KEY);
		if (!TextUtils.isEmpty(openingHours)) {
			mOpeningHoursView.setText(openingHours);
		} else {
			ParseHelper.fetchBusiness(new GetCallback<Business>() {

				@Override
				public void done(Business business, ParseException e) {
					mOpeningHoursView.setText(business.getOpeningHours());
					mPage.getData().putString(PhoneOpeningHoursPage.OPENING_HOURS_DATA_KEY,
							business.getOpeningHours());
					mPage.notifyDataChanged();
				}
			});
		}
		
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		if (!(activity instanceof PageFragmentCallbacks)) {
			throw new ClassCastException(
					"Activity must implement PageFragmentCallbacks");
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
			public void beforeTextChanged(CharSequence charSequence, int i,
					int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1,
					int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData().putString(PhoneOpeningHoursPage.PHONE_DATA_KEY,
						(editable != null) ? editable.toString() : null);
				mPage.notifyDataChanged();
			}
		});

		mOpeningHoursView.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i,
					int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1,
					int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mPage.getData().putString(
						PhoneOpeningHoursPage.OPENING_HOURS_DATA_KEY,
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
			InputMethodManager imm = (InputMethodManager) getActivity()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			if (!menuVisible) {
				imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
			}
		}
	}
}
