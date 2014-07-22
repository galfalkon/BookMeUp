package com.gling.bookmeup.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

/**
 * Represent a fragment that contains buttons that trigger a fragment shift.
 */
public abstract class OnClickListenerFragment extends Fragment implements OnClickListener {
	private static final String TAG = "OnClickListenerFragment";
	
	abstract protected int getFragmentLayoutId();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView");
		
		View rootView = inflater.inflate(getFragmentLayoutId(), container, false);

		for (View v : rootView.getTouchables()) {
			if (v.isClickable()) {
				v.setOnClickListener(this);
			}
		}
		
		return rootView;
	}
}
