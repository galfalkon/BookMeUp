package com.gling.bookmeup.login;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.gling.bookmeup.R;
import com.gling.bookmeup.business.Business;
import com.gling.bookmeup.business.BusinessMainActivity;
import com.gling.bookmeup.customer.Customer;
import com.gling.bookmeup.customer.CustomerMainActivity;
import com.gling.bookmeup.main.OnClickListenerFragment;
import com.gling.bookmeup.main.FragmentsFlowManager;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class EMailLoginFragment extends OnClickListenerFragment {

    private static final String TAG = "EMailLoginFragment";

    private EditText edtUserName, edtPassword;

    public int getFragmentLayoutId() {
        return R.layout.login_email_fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        edtPassword = (EditText) view.findViewById(R.id.email_login_edtPassword);
        edtUserName = (EditText) view.findViewById(R.id.email_login_edtUserName);

        return view;
    }

    @Override
    public void onClick(View v) {
        final int viewClickedId = v.getId();

        switch (viewClickedId) {
        case R.id.email_login_btnContinue:
            handleLoginReuest();
            break;
        }
    }

    private void handleLoginReuest() {
        Log.i(TAG, "handleLoginReuest");

        String userName = edtUserName.getText().toString();
        String password = edtPassword.getText().toString();

        Log.i(TAG, "Showing a progress dialog");
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Loging in...");

        ParseUser.logInInBackground(userName, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    progressDialog.dismiss();
                    Log.i(TAG, "Login failed: " + e.getMessage());
                    Toast.makeText(getActivity(), "Login failed: " + e.toString(), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!user.getBoolean("emailVerified")) {
                    progressDialog.dismiss();
                    Log.i(TAG, "User hasn't verified Email address");
                    Toast.makeText(getActivity(), "Please verifiy your Email address", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.i(TAG, "User '" + user.getUsername() +  "' logged in");
                if (user.getParseObject(Business.CLASS_NAME) != null) {
                    Intent intent = new Intent(getActivity(), BusinessMainActivity.class);
                    startActivity(intent);
                    return;
                }
                
                if (user.getParseObject(Customer.CLASS_NAME) != null) {
                    Intent intent = new Intent(getActivity(), CustomerMainActivity.class);
                    startActivity(intent);
                    return;
                }
                // loginActivity.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                FragmentsFlowManager.goToNextFragment(getActivity(), R.id.email_login_btnContinue);
                progressDialog.dismiss();
            }
        });
    }
}
