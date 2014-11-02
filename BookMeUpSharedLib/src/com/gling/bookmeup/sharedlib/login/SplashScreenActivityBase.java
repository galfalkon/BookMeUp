package com.gling.bookmeup.sharedlib.login;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import com.gling.bookmeup.sharedlib.R;
import com.gling.bookmeup.sharedlib.parse.ParseHelper.Category;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseQuery;

public abstract class SplashScreenActivityBase extends Activity {

    private static final String TAG = "SplashScreenActivity";

    protected static final String EXTRA_PUSH_NOTIFICATION_DATA = "com.parse.Data";

    protected abstract void goToNextActivity();

    protected abstract void handlePushNotification();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");

        super.onCreate(savedInstanceState);

        // Track application opens
        ParseAnalytics.trackAppOpened(getIntent());

        setContentView(R.layout.login_splash_screen_activity);
    }

    @Override
    protected void onResume() {
        super.onResume();

        goToNextActivityIfConnected();
    }

    private void goToNextActivityIfConnected() {
        if (!isNetworkAvailable()) {
            Log.i(TAG, "No internet connection");
            showNoInternetDialog();
            return;
        }

        new Thread() {
            public void run() {
                try {
                    // Fetch business categories
                    ParseQuery<Category> query = ParseQuery.getQuery(Category.CLASS_NAME);
                    List<Category> categories = query.find();
                    Category.setCategories(categories);

                    goToNextActivity();
                } catch (ParseException e) {
                    Log.e(TAG, "Exception: " + e.getMessage());
                }
            }
        }.start();
    }

    private void showNoInternetDialog() {
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
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}