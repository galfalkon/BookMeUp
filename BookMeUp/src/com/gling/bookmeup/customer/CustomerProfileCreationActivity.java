package com.gling.bookmeup.customer;

import com.gling.bookmeup.R;
import com.gling.bookmeup.sharedlib.parse.Customer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class CustomerProfileCreationActivity extends Activity implements OnClickListener, TextWatcher, OnEditorActionListener 
{
	private static final String TAG = "CustomerProfileCreationActivity";
	
	private EditText _edtPhoneNumber;
	private Button _btnDone;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		Log.i(TAG, "onCreate");
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.customer_profile_creation_activity);
		
		_edtPhoneNumber = (EditText) findViewById(R.id.customer_profile_creation_edtPhoneNumber);
		_edtPhoneNumber.addTextChangedListener(this);
		_edtPhoneNumber.setOnEditorActionListener(this);
		_edtPhoneNumber.requestFocus();
		
		_btnDone = (Button)findViewById(R.id.customer_profile_creation_btnDone);
		_btnDone.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) 
	{
		switch (v.getId())
		{
		case R.id.customer_profile_creation_btnDone:
			handleDoneClick();
			break;
		}
	}
	
	@Override
	public void afterTextChanged(Editable s) 
	{
		_btnDone.setEnabled(!s.toString().isEmpty());
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) 
	{
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) 
	{
	}
	
	private boolean validatePhoneNumber()
	{
		String phoneNumber = _edtPhoneNumber.getText().toString();
		return (phoneNumber.length() >= 10 && phoneNumber.length() <= 13);
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) 
	{
		switch (actionId)
		{
		case EditorInfo.IME_ACTION_DONE:
			handleDoneClick();
			return true;
		default:
			return false;
		}
	}
	
	private void handleDoneClick()
	{
		if (validatePhoneNumber())
		{
			Customer currentCustomer = Customer.getCurrentCustomer();
			currentCustomer.setPhoneNumber(_edtPhoneNumber.getText().toString());
			currentCustomer.saveInBackground();
			startActivity(new Intent(this, CustomerMainActivity.class));
		}
		else
		{
			_edtPhoneNumber.setError(getString(R.string.customer_profile_creation_edtPhoneNumberError));
		}
	}
}
