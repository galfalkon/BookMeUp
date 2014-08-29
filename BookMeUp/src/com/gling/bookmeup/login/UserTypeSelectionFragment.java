package com.gling.bookmeup.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.gling.bookmeup.R;
import com.gling.bookmeup.business.Business;
import com.gling.bookmeup.business.BusinessMainActivity;
import com.gling.bookmeup.customer.Customer;
import com.gling.bookmeup.customer.CustomerMainActivity;
import com.gling.bookmeup.main.OnClickListenerFragment;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class UserTypeSelectionFragment extends OnClickListenerFragment implements OnClickListener {
    private static final String TAG = "UserTypeSelectionFragment";

    public int getFragmentLayoutId() {
        return R.layout.login_user_type_selection_fragment;
    }

    //	@Override
    //	public View onCreateView(LayoutInflater inflater, ViewGroup container,
    //			Bundle savedInstanceState) {
    //		super.onCreateView(inflater, container, savedInstanceState);
    //		Log.i(TAG, "onCreateView");
    //
    //		View rootView = inflater.inflate(R.layout.fragment_user_type_selection,
    //				container, false);
    //		
    //		// Set event listeners
    //		rootView.findViewById(R.id.user_type_selection_btnBusiness).setOnClickListener(this);
    //		rootView.findViewById(R.id.user_type_selection_btnCustomer).setOnClickListener(this);
    //
    //		return rootView;
    //	}

    @Override
    public void onClick(View v) {

        ParseUser currentUser = ParseUser.getCurrentUser();
        
        switch (v.getId()) {
        case R.id.user_type_selection_btnBusiness:
            Log.i(TAG, "btnBusiness clicked");
            Business business = new Business();
            currentUser.put(Business.CLASS_NAME, business);
            //business.setUser(ParseUser.getCurrentUser());

            final ProgressDialog progressDialogBusiness = ProgressDialog.show(getActivity(), null, "Creating business...");
            currentUser.saveInBackground(new SaveCallback() {

                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        progressDialogBusiness.dismiss();
                        Intent intent = new Intent(getActivity(), BusinessMainActivity.class);
                        startActivity(intent);
                    } else {
                        progressDialogBusiness.dismiss();
                        Toast.makeText(getActivity(), "oops, we're having difficulties, please try again...", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "Business creation failed: " + e.getMessage());
                    }
                }
            });
            break;
        case R.id.user_type_selection_btnCustomer:
            Log.i(TAG, "btnBusiness clicked");
            Customer customer = new Customer();
            currentUser.put(Customer.CLASS_NAME, customer);
            //customer.setUser(ParseUser.getCurrentUser());

            final ProgressDialog progressDialogCustomer = ProgressDialog.show(getActivity(), null, "Creating customer...");
            currentUser.saveInBackground(new SaveCallback() {

                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        progressDialogCustomer.dismiss();
                        Intent intent = new Intent(getActivity(), CustomerMainActivity.class);
                        startActivity(intent);
                    } else {
                        progressDialogCustomer.dismiss();
                        Toast.makeText(getActivity(), "oops, we're having difficulties, please try again...", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "Customer creation failed: " + e.getMessage());
                    }
                }
            });
            break;
        }
    }
}
