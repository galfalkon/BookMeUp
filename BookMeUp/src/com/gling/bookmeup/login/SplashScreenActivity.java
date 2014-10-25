package com.gling.bookmeup.login;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.gling.bookmeup.R;
import com.gling.bookmeup.business.BusinessMainActivity;
import com.gling.bookmeup.customer.CustomerMainActivity;
import com.gling.bookmeup.main.PushUtils;
import com.gling.bookmeup.sharedlib.parse.Business;
import com.gling.bookmeup.sharedlib.parse.Customer;
import com.gling.bookmeup.sharedlib.parse.ParseHelper;
import com.gling.bookmeup.sharedlib.parse.ParseHelper.Category;
import com.gling.bookmeup.sharedlib.parse.ParseHelper.User;
import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.RefreshCallback;

public class SplashScreenActivity extends Activity {

    private static final String TAG = "SplashScreenActivity";

    private static final String EXTRA_PUSH_NOTIFICATION_DATA = "com.parse.Data";

    public final static String EXTRA_FIRST_FRAGMENT = "com.gling.bookmeup.FIRST_FRAGMENT";
    public final static String EXTRA_MESSAGE = "com.gling.bookmeup.MESSAGE";

    private Activity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        mContext = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_splash_screen_activity);

        // Track application opens
        ParseAnalytics.trackAppOpened(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();

        goToNextActivityIfConnected();
    }

    private void goToNextActivityIfConnected() {
        if (!isNetworkAvailable()) {
            Log.i(TAG, "No internet connection");

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.error_no_internet_connection);
            builder.setIconAttribute(android.R.attr.alertDialogIcon);
            builder.setPositiveButton(R.string.ok, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.i(TAG, "ok click");
                    finish();
                }
            });
            builder.setNegativeButton(R.string.try_again, new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.i(TAG, "Try Again click");
                    goToNextActivityIfConnected();
                }
            });
            builder.create().show();
            return;
        }
        fetchBusinessCategories();
        goToNextActivity();
    }

    private void fetchBusinessCategories() {
        ParseQuery<Category> query = ParseQuery.getQuery(Category.CLASS_NAME);
        query.findInBackground(new FindCallback<Category>() {
            public void done(List<Category> categories, ParseException e) {
                if (e == null) {
                    Category.setCategories(categories);
                } else {
                    Log.e(TAG, "Error: " + e.getMessage());
                }
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void goToNextActivity() {
        final Intent intent;

        if (ParseHelper.isUserLoggedIn()) {
            ParseUser user = ParseUser.getCurrentUser();
            Log.i(TAG, "User '" + user.getUsername() + "' is found to be logged in");

            try {
                // Fetch current business
                ParseObject businessParseObject = user.getParseObject(User.Keys.BUSINESS_POINTER);
                if (businessParseObject != null) {
                    Log.i(TAG, "Fetching current business");
                    Business currentBusiness = businessParseObject.fetchIfNeeded();
                    Category category = currentBusiness.getCategory();
                    if (category != null) {
                        category.fetchIfNeeded();
                    }
                    Business.setCurrentBusiness(currentBusiness);
                }

                // Fetch current customer
                ParseObject customerParseObject = user.getParseObject(User.Keys.CUSTOMER_POINTER);
                if (customerParseObject != null) {
                    Log.i(TAG, "Fetching current customer");
                    Customer currentCustomer = customerParseObject.fetchIfNeeded();
                    Customer.setCurrentCustomer(currentCustomer);
                }
            } catch (ParseException e) {
                Log.e(TAG, "Exception: " + e.getMessage());
                intent = new Intent(mContext, LoginMainActivity.class);
                startActivity(intent);
                return;
            }

            // Handle push notification if needed
            Bundle extras = getIntent().getExtras();
            if ((extras != null) && extras.containsKey(EXTRA_PUSH_NOTIFICATION_DATA)) {
                handlePushNotification();
                return;
            }

            if (!ParseHelper.isEmailVerified()) {
                Log.i(TAG, "User '" + user.getUsername() + "' mail is not verified");
                intent = new Intent(mContext, LoginMainActivity.class);
                intent.putExtra(EXTRA_MESSAGE,
                                "Please verify your Email address\nYour registered mail is: "
                                        + user.getEmail());
            } else if (Business.getCurrentBusiness() != null
                    && Customer.getCurrentCustomer() != null) {
                Log.i(TAG, "User '" + user.getUsername()
                        + "' is associated with both customer and business");
                intent = new Intent(mContext, LoginMainActivity.class);
                intent.putExtra(EXTRA_FIRST_FRAGMENT, "UserTypeSelectionFragment");
            } else if (Business.getCurrentBusiness() != null) {
                Log.i(TAG, "User '" + user.getUsername() + "' is associated with a business");

                if (TextUtils.isEmpty(Business.getCurrentBusiness().getName())) {
                    intent = new Intent(mContext, LoginMainActivity.class);
                } else {
                    intent = new Intent(mContext, BusinessMainActivity.class);
                }
            } else if (Customer.getCurrentCustomer() != null) {
                Log.i(TAG, "User '" + user.getUsername() + "' is associated with a customer");
                intent = new Intent(mContext, CustomerMainActivity.class);
            } else {
                Log.i(TAG, "User '" + user.getUsername()
                        + "' is not associated with a business or a customer");
                intent = new Intent(mContext, LoginMainActivity.class);
            }
            user.refreshInBackground(new RefreshCallback() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if (e != null) {
                        Log.e(TAG, e.getMessage());
                    }
                    startActivity(intent);
                }
            });
        } else {
            intent = new Intent(mContext, LoginMainActivity.class);
            startActivity(intent);
        }
    }

    private void handlePushNotification() {
        try {
            JSONObject json = new JSONObject(getIntent().getExtras()
                                                        .getString(EXTRA_PUSH_NOTIFICATION_DATA));
            String action = json.getString("action");
            Log.i(TAG, "Push action: " + action);

            PushUtils.PushNotificationType pushType = PushUtils.PushNotificationType
                                                                                    .valueOfAction(action);
            // TODO: Check if activity is already running
            Intent intent = new Intent(getApplicationContext(), pushType._activity);
            pushType.putIntoIntent(intent);
            startActivity(intent);
        } catch (JSONException e) {
            Log.e(TAG, "Exception: " + e.getMessage());
            return;
        }
    }
}
