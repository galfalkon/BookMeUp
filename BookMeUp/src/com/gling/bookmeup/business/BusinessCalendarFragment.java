package com.gling.bookmeup.business;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.gling.bookmeup.R;
import com.gling.bookmeup.main.OnClickListenerFragment;

public class BusinessCalendarFragment extends OnClickListenerFragment implements OnClickListener {

	private static final String TAG = "BusinessCalendarFragment";

	@Override
	protected int getFragmentLayoutId() {
		return R.layout.business_calendar_fragment;
	}

	@Override
	public void onClick(View v) {
		Toast.makeText(getActivity(), "Not implemented", Toast.LENGTH_SHORT).show();
	}
}
