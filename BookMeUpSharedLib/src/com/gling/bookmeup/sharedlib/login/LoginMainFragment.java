package com.gling.bookmeup.sharedlib.login;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.gling.bookmeup.main.FragmentsManagerUtils;
import com.gling.bookmeup.main.OnClickListenerFragment;
import com.gling.bookmeup.sharedlib.R;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class LoginMainFragment extends OnClickListenerFragment {
    private static final String TAG = "LoginFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.findViewById(R.id.login_txtContact)
        .setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent localIntent = new Intent("android.intent.action.SENDTO");
                localIntent.setType("message/rfc822");
                localIntent.setData(Uri.parse("mailto:support@bookmeup.com"));
                localIntent.putExtra("android.intent.extra.SUBJECT", getActivity().getString(R.string.feedback_subject));
                localIntent.putExtra("android.intent.extra.TEXT", getActivity().getString(R.string.feedback_text));
                try {
                    startActivity(Intent.createChooser(localIntent, getActivity().getString(R.string.send_feedback)));
                } catch (ActivityNotFoundException localActivityNotFoundException) {
                	Crouton.showText(getActivity(), R.string.no_email_clients_installed, Style.ALERT);
                }
            }
        });
        return view;
    }

    @Override
    protected int getFragmentLayoutId() {
        return R.layout.login_main_fragment;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
		if (id == R.id.login_btnLoginWithEMail) 
		{
			Log.i(TAG, "btnLoginWithEMail clicked");
			// TODO popup if someone is already logged in
			FragmentsManagerUtils.goToNextFragment(getActivity(), R.id.login_container, ((LoginMainActivityBase)getActivity()).getEmailLoginFragmentInstance());
		}
		else if (id == R.id.login_btnSignUp) 
		{
			Log.i(TAG, "btnSignUp clicked");
			FragmentsManagerUtils.goToNextFragment(getActivity(), R.id.login_container, ((LoginMainActivityBase)getActivity()).getEmailSignUpFragmentInstance());
		}
    }
}