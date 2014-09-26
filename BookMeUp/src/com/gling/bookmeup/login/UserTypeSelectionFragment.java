package com.gling.bookmeup.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.gling.bookmeup.R;
import com.gling.bookmeup.business.Business;
import com.gling.bookmeup.business.BusinessMainActivity;
import com.gling.bookmeup.customer.Customer;
import com.gling.bookmeup.customer.CustomerMainActivity;
import com.gling.bookmeup.main.OnClickListenerFragment;
import com.gling.bookmeup.main.ParseHelper;
import com.gling.bookmeup.main.ParseHelper.User;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class UserTypeSelectionFragment extends OnClickListenerFragment implements OnClickListener {
    private static final String TAG = "UserTypeSelectionFragment";

    public int getFragmentLayoutId() {
        return R.layout.login_user_type_selection_fragment;
    }

    @Override
    public void onClick(View v) {

        ParseUser currentUser = ParseUser.getCurrentUser();

        switch (v.getId()) {
        case R.id.user_type_selection_btnBusiness:
            Log.i(TAG, "btnBusiness clicked");
            
            if (currentUser.getParseObject(User.Keys.BUSINESS_POINTER) != null) {
                Intent intent = new Intent(getActivity(), BusinessMainActivity.class);
                startActivity(intent);
                return;
            };
            
            Business business = new Business();
            currentUser.put(ParseHelper.User.Keys.BUSINESS_POINTER, business);

            final ProgressDialog progressDialogBusiness = ProgressDialog.show(getActivity(), null,
                    "Creating your business profile...");
            currentUser.saveInBackground(new SaveCallback() {

                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        progressDialogBusiness.dismiss();
                        Intent intent = new Intent(getActivity(), BusinessMainActivity.class);
                        startActivity(intent);
                    } else {
                        progressDialogBusiness.dismiss();
                        Crouton.showText(getActivity(), "Oops, we're having difficulties, please try again...", Style.ALERT);
                        Log.i(TAG, "Business creation failed: " + e.getMessage());
                    }
                }
            });
            break;
        case R.id.user_type_selection_btnCustomer:
            Log.i(TAG, "btnBusiness clicked");
            
            if (currentUser.getParseObject(User.Keys.CUSTOMER_POINTER) != null) {
                Intent intent = new Intent(getActivity(), CustomerMainActivity.class);
                startActivity(intent);
                return;
            };
            
            Customer customer = new Customer();
            currentUser.put(ParseHelper.User.Keys.CUSTOMER_POINTER, customer);
            //customer.setUser(ParseUser.getCurrentUser());

            final ProgressDialog progressDialogCustomer = ProgressDialog.show(getActivity(), null,
                    "Creating your customer profile...");
            currentUser.saveInBackground(new SaveCallback() {

                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        progressDialogCustomer.dismiss();
                        Intent intent = new Intent(getActivity(), CustomerMainActivity.class);
                        startActivity(intent);
                    } else {
                        progressDialogCustomer.dismiss();
                        Crouton.showText(getActivity(), "Oops, we're having difficulties, please try again...", Style.ALERT);
                        Log.i(TAG, "Customer creation failed: " + e.getMessage());
                    }
                }
            });
            break;
        }
    }
}
