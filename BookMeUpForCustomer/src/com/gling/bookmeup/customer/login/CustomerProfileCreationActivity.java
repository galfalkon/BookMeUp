package com.gling.bookmeup.customer.login;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.gling.bookmeup.customer.CustomerMainActivity;
import com.gling.bookmeup.customer.R;
import com.gling.bookmeup.sharedlib.parse.Customer;
import com.gling.bookmeup.sharedlib.parse.ParseHelper;
import com.parse.ParseObject;

public class CustomerProfileCreationActivity extends Activity implements OnClickListener,
        TextWatcher, OnEditorActionListener {
    private static final String TAG = "CustomerProfileCreationActivity";

    private EditText _edtName, _edtPhoneNumber;
    private Button _btnDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_profile_creation_activity);

        _edtName = (EditText) findViewById(R.id.customer_profile_creation_edtName);
        _edtName.requestFocus();
        _edtName.addTextChangedListener(this);

        _edtPhoneNumber = (EditText) findViewById(R.id.customer_profile_creation_edtPhoneNumber);
        _edtPhoneNumber.addTextChangedListener(this);
        _edtPhoneNumber.setOnEditorActionListener(this);

        _btnDone = (Button) findViewById(R.id.customer_profile_creation_btnDone);
        _btnDone.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.customer_profile_creation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
        case R.id.action_logout:
            ParseHelper.logOut();
            intent = new Intent(this, CustomerLoginMainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.customer_profile_creation_btnDone:
            handleDoneClick();
            break;
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        _btnDone.setEnabled(!_edtName.getText().toString().isEmpty()
                && !_edtPhoneNumber.getText().toString().isEmpty());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    private boolean validateName() {
        String name = _edtName.getText().toString();
        return !name.isEmpty();
    }

    private boolean validatePhoneNumber() {
        String phoneNumber = _edtPhoneNumber.getText().toString();
        return (phoneNumber.length() >= 10 && phoneNumber.length() <= 13);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        switch (actionId) {
        case EditorInfo.IME_ACTION_DONE:
            handleDoneClick();
            return true;
        default:
            return false;
        }
    }

    private void handleDoneClick() {
        if (!validateName()) {
            _edtName.setError(getString(R.string.customer_profile_creation_edtNameError));
        }
        if (!validatePhoneNumber()) {
            _edtPhoneNumber
                           .setError(getString(R.string.customer_profile_creation_edtPhoneNumberError));
            return;
        }

        Customer currentCustomer = Customer.getCurrentCustomer();
        currentCustomer.setName(_edtName.getText().toString());
        currentCustomer.setPhoneNumber(_edtPhoneNumber.getText().toString());
        currentCustomer.setFavourites(new ArrayList<ParseObject>());
        currentCustomer.saveInBackground();
        startActivity(new Intent(this, CustomerMainActivity.class));
    }
}
