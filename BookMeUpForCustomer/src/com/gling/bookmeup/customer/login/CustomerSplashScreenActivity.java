package com.gling.bookmeup.customer.login;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.gling.bookmeup.customer.BookMeUpForCustomerApplication;
import com.gling.bookmeup.customer.CustomerMainActivity;
import com.gling.bookmeup.main.PushUtils;
import com.gling.bookmeup.sharedlib.login.SplashScreenActivityBase;
import com.gling.bookmeup.sharedlib.parse.Customer;
import com.gling.bookmeup.sharedlib.parse.ParseHelper;
import com.gling.bookmeup.sharedlib.parse.ParseHelper.User;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class CustomerSplashScreenActivity extends SplashScreenActivityBase {
    private static final String TAG = "CustomerSplashScreenActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
    }

    @Override
    protected void goToNextActivity() {
        if (!ParseHelper.isUserLoggedIn()) {
            startActivity(new Intent(this, CustomerLoginMainActivity.class));
            return;
        }

        ParseUser user = ParseUser.getCurrentUser();
        Log.i(TAG, "User '" + user.getUsername() + "' is found to be logged in");
        if (!ParseHelper.isEmailVerified()) {
            Log.i(TAG, "User '" + user.getUsername() + "' mail is not verified");
            startActivity(new Intent(this, CustomerLoginMainActivity.class));
            return;
        }

        try {
            // Refresh user
            user.refresh();

            // Fetch current business
            ParseObject customerParseObject = user.getParseObject(User.Keys.CUSTOMER_POINTER);
            if (customerParseObject == null) {
                // TODO: Create Customer
                Crouton
                       .showText(this,
                                 "TODO: Create Customer (Current user isn't associated with a Customer instance)",
                                 Style.INFO);
                return;
            }
            Log.i(TAG, "User '" + user.getUsername() + "' is associated with a customer");

            Customer currentCustomer = customerParseObject.fetchIfNeeded();
            Customer.setCurrentCustomer(currentCustomer);

            // Handle push notification if needed
            Bundle extras = getIntent().getExtras();
            if ((extras != null) && extras.containsKey(EXTRA_PUSH_NOTIFICATION_DATA)) {
                handlePushNotification();
                return;
            }

            if (TextUtils.isEmpty(Customer.getCurrentCustomer().getName())) {
                startActivity(new Intent(this, CustomerProfileCreationActivity.class));
            } else {
                startActivity(new Intent(this, CustomerMainActivity.class));
            }
        } catch (ParseException e) {
            Log.e(TAG, "Exception: " + e.getMessage());
            startActivity(new Intent(this, CustomerLoginMainActivity.class));
        }
    }

    @Override
    protected void handlePushNotification() {
        try {
            JSONObject json = new JSONObject(getIntent().getExtras()
                                                        .getString(EXTRA_PUSH_NOTIFICATION_DATA));
            String action = json.getString("action");
            Log.i(TAG, "Push action: " + action);

            PushUtils.PushNotificationType pushType = PushUtils.PushNotificationType
                                                                                    .valueOfAction(action);
            Intent intent = new Intent(BookMeUpForCustomerApplication.getContext(), CustomerMainActivity.class);
            pushType.putIntoIntent(intent);
            startActivity(intent);
        } catch (JSONException e) {
            Log.e(TAG, "Exception: " + e.getMessage());
            return;
        }
    }
}
