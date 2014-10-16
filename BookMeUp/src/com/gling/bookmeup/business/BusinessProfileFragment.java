package com.gling.bookmeup.business;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.gling.bookmeup.R;
import com.gling.bookmeup.business.wizards.BusinessProfileWizardActivity;
import com.gling.bookmeup.main.FragmentsFlowManager;
import com.gling.bookmeup.main.OnClickListenerFragment;

public class BusinessProfileFragment extends OnClickListenerFragment {

    private static final String TAG = "BusinessProfileFragment";

    private Button btnLaunchWizard;
    
    @Override
    protected int getFragmentLayoutId() {
        return R.layout.business_profile_fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        btnLaunchWizard = (Button) view.findViewById(R.id.business_profile_wizard_launch);
        
        if (savedInstanceState == null) {
            Log.i(TAG, "initProfileDetails");
        }
        
        btnLaunchWizard.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), BusinessProfileWizardActivity.class);
				startActivity(intent);
			}
		});

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.business_profile_wizard_launch:
            break;
        }
        FragmentsFlowManager.goToNextFragment(getActivity(), R.id.container, v.getId());
    }

}

// private static byte[] getByteArrayFromImageView(ImageView imgView) {
// Bitmap bitmap = ((BitmapDrawable)imgView.getDrawable()).getBitmap();
// ByteArrayOutputStream stream = new ByteArrayOutputStream();
// bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
// return stream.toByteArray();
// }