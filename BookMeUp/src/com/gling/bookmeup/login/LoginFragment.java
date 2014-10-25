package com.gling.bookmeup.login;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.gling.bookmeup.R;
import com.gling.bookmeup.main.FragmentsManagerUtils;
import com.gling.bookmeup.main.OnClickListenerFragment;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class LoginFragment extends OnClickListenerFragment {
    private static final String TAG = "LoginFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view
        .findViewById(R.id.login_txtContact)
        .setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent localIntent = new Intent("android.intent.action.SENDTO");
                localIntent.setType("message/rfc822");
                localIntent.setData(Uri.parse("mailto:support@bookmeup.com"));
                localIntent.putExtra("android.intent.extra.SUBJECT", "Feedback for Android BookMeUp! App");
                localIntent.putExtra("android.intent.extra.TEXT", "Dear BookMeUp!,\n\n");
                try {
                    startActivity(Intent.createChooser(localIntent, "Send mail..."));
                } catch (ActivityNotFoundException localActivityNotFoundException) {
                	Crouton.showText(getActivity(), "There are no email clients installed", Style.ALERT);
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
        switch (v.getId()) {
        case R.id.login_btnLoginWithEMail:
            Log.i(TAG, "btnLoginWithEMail clicked");
            // TODO popup if someone is already logged in
            FragmentsManagerUtils.goToNextFragment(getActivity(), R.id.login_container, new EMailLoginFragment());
            break;
        case R.id.login_btnSignUp:
            Log.i(TAG, "btnSignUp clicked");
            FragmentsManagerUtils.goToNextFragment(getActivity(), R.id.login_container, new EMailSignUpFragment());
            break;
        }
    }
}