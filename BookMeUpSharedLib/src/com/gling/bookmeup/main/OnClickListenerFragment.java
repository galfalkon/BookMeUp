package com.gling.bookmeup.main;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;

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
			    // we get: java.lang.RuntimeException: Don't call setOnClickListener for an AdapterView. You probably want setOnItemClickListener instead
			    // maybe apply this only to buttons?
			    try {
			        AdapterView av = (AdapterView) v;
			    } catch (ClassCastException e) {
			        v.setOnClickListener(this);
			    }
			}
		}
		
		return rootView;
	}
}
