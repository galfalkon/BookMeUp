package com.gling.bookmeup.fragments;

import java.io.ByteArrayOutputStream;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.gling.bookmeup.R;
import com.gling.bookmeup.main.FragmentsFlowManager;
import com.gling.bookmeup.main.OnClickListenerFragment;
import com.gling.bookmeup.main.ParseHelper.BusinessesClass;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class BusinessProfileCreationFragment extends OnClickListenerFragment {
	
	private static final String TAG = "BusinessProfileCreationFragment";
	
	private EditText edtBusinessName, edtBusinessDescription, edtBusinessOpeningHours;
	private ImageView imgBusinessImage;
	private ListView lstBusinessServices;
	
	@Override
	protected int getFragmentLayoutId() {
		return R.layout.fragment_business_profile_creation;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		
		edtBusinessName = (EditText)view.findViewById(R.id.business_profile_creation_edtBusinessName);
		imgBusinessImage = (ImageView)view.findViewById(R.id.business_profile_creation_imgBusinessImage);
		edtBusinessDescription = (EditText)view.findViewById(R.id.business_profile_creation_edtBusinessDescription);
		edtBusinessOpeningHours = (EditText)view.findViewById(R.id.business_profile_creation_edtBusinessOpeningHours);
		lstBusinessServices = (ListView)view.findViewById(R.id.business_profile_creation_lstBusinessServices);
		
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.business_profile_creation_btnCreate:
			Log.i(TAG, "business_profile_creation_btnCreate clicked");
			if (!validateInput()) {
				Log.i(TAG, "invalid input");
				return;
			}
			if (!userInCache()) {
				Log.i(TAG, "user not found in cache, redirecting to login...");
				Toast.makeText(getActivity(), "Please sign up or log in first...", Toast.LENGTH_SHORT).show();
				getActivity().getSupportFragmentManager().
				beginTransaction().
				addToBackStack(null).
				replace(R.id.container, Fragment.instantiate(getActivity(), LoginFragment.class.getName())).
				commit();
				return;
			}
			createBusiness();
			break;
		}
		FragmentsFlowManager.goToNextFragment(getActivity(), v.getId());
	}
	
	private boolean userInCache() {
		ParseUser currentUser = ParseUser.getCurrentUser();
		return (currentUser != null);
	}

	private boolean validateInput() {
		return true;
	}

	private void createBusiness() {
		ParseUser currentUser = ParseUser.getCurrentUser();
		Log.i(TAG, "current user is: " + currentUser.getUsername());
		
		ParseObject newBusiness = new ParseObject(BusinessesClass.CLASS_NAME);
		newBusiness.put(BusinessesClass.Keys.USERNAME, currentUser.getUsername());
		newBusiness.put(BusinessesClass.Keys.NAME, edtBusinessName.getText().toString());
		newBusiness.put(BusinessesClass.Keys.DESCRIPTION, edtBusinessDescription.getText().toString());
		newBusiness.put(BusinessesClass.Keys.OPENING_HOURS, edtBusinessOpeningHours.getText().toString());
		newBusiness.put(BusinessesClass.Keys.IMAGE, getByteArrayFromImageView(imgBusinessImage)); // TODO move to utils
		
		final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...");
		newBusiness.saveInBackground(new SaveCallback() {	
			@Override
			public void done(ParseException e) {
				progressDialog.dismiss();
				Log.i(TAG, "Done creating new business");
				if (e != null) {
					Log.e(TAG, "Exception occurred: " + e.getMessage());
					return;
				}
			}
		});
	}

	private static byte[] getByteArrayFromImageView(ImageView imgView) {
		Bitmap bitmap = ((BitmapDrawable)imgView.getDrawable()).getBitmap();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		return stream.toByteArray();
	}
}
