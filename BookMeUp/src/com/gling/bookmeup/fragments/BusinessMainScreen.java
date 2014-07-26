package com.gling.bookmeup.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

import com.gling.bookmeup.R;
import com.gling.bookmeup.main.MainActivity;
import com.gling.bookmeup.main.OnClickListenerFragment;

public class BusinessMainScreen extends OnClickListenerFragment implements OnClickListener {

	private static final String TAG = "BusinessMainActivity";

	@Override
	protected int getFragmentLayoutId() {
		return R.layout.fragment_business_main_screen;
	}

	@Override
	public void onClick(View v) {
		Toast.makeText(getActivity(), "Not implemented", Toast.LENGTH_SHORT).show();
	}
}
