package com.gling.bookmeup.main;

import android.app.Activity;
import android.app.Fragment;
import android.util.Log;

public class FragmentsManagerUtils
{
	private static final String TAG = "FragmentsManagerUtils";

	public static void goToNextFragment(Activity activity, int container,
			Fragment fragmentToGoTo)
	{
		Log.i(TAG, "goToNextFragment");

		activity.getFragmentManager().beginTransaction().addToBackStack(null)
				.replace(container, fragmentToGoTo).commit();
	}
}
