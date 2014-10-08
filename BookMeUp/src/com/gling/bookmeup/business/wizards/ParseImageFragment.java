package com.gling.bookmeup.business.wizards;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gling.bookmeup.R;
import com.gling.bookmeup.business.Business;
import com.gling.bookmeup.main.BookMeUpApplication;
import com.gling.bookmeup.main.ParseHelper;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.tech.freak.wizardpager.model.Page;
import com.tech.freak.wizardpager.ui.PageFragmentCallbacks;

public class ParseImageFragment extends Fragment {

	private static final String NEW_IMAGE_URI = "new_image_uri";
	private static final int GALLERY_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;

	protected static final String ARG_KEY = "key";

	private PageFragmentCallbacks mCallbacks;
	private String mKey;
	private Page mPage;

	private ParseImageView imageView;

	private Uri mNewImageUri;

	public static ParseImageFragment create(String key) {
		Bundle args = new Bundle();
		args.putString(ARG_KEY, key);

		ParseImageFragment f = new ParseImageFragment();
		f.setArguments(args);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle args = getArguments();
		mKey = args.getString(ARG_KEY);
		mPage = mCallbacks.onGetPage(mKey);

		if (savedInstanceState != null) {
			String uriString = savedInstanceState.getString(NEW_IMAGE_URI);
			if (!TextUtils.isEmpty(uriString)) {
				mNewImageUri = Uri.parse(uriString);
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mNewImageUri != null) {
			outState.putString(NEW_IMAGE_URI, mNewImageUri.toString());
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.business_profile_wizard_parse_image_fragment,
				container, false);
		((TextView) rootView.findViewById(android.R.id.title)).setText(mPage
				.getTitle());

		imageView = (ParseImageView) rootView.findViewById(R.id.parseImageView);

		imageView.setPlaceholder(BookMeUpApplication.getContext().getResources().getDrawable(R.drawable.ic_person));
		
		String imageUri = mPage.getData().getString(Page.SIMPLE_DATA_KEY);
		if (!TextUtils.isEmpty(imageUri)) {
			imageView.setImageURI(Uri.parse(imageUri));
		} else {
			ParseHelper.fetchBusiness(new GetCallback<Business>() {
				
				@Override
				public void done(Business business, ParseException e) {
					ParseFile imageFile = business.getImageFile();
					if (imageFile != null) {
						imageView.setParseFile(imageFile);
						imageView.loadInBackground();
					}
				}
			});
		}
		
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogFragment pickPhotoSourceDialog = new DialogFragment() {
					@Override
					public Dialog onCreateDialog(Bundle savedInstanceState) {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								getActivity());
						builder.setItems(R.array.image_photo_sources,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										switch (which) {
										case 0:
											// Gallery
											Intent photoPickerIntent = new Intent(
													Intent.ACTION_GET_CONTENT);
											photoPickerIntent
													.setType("image/*");
											startActivityForResult(
													photoPickerIntent,
													GALLERY_REQUEST_CODE);
											break;

										default:
											// Camera
											mNewImageUri = getActivity()
													.getContentResolver()
													.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
															new ContentValues());
											Intent photoFromCamera = new Intent(
													MediaStore.ACTION_IMAGE_CAPTURE);
											photoFromCamera.putExtra(
													MediaStore.EXTRA_OUTPUT,
													mNewImageUri);
											photoFromCamera
													.putExtra(
															MediaStore.EXTRA_VIDEO_QUALITY,
															0);
											startActivityForResult(
													photoFromCamera,
													CAMERA_REQUEST_CODE);
											break;
										}

									}
								});
						return builder.create();
					}
				};

				pickPhotoSourceDialog.show(getFragmentManager(),
						"pickPhotoSourceDialog");
			}
		});

		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		if (!(activity instanceof PageFragmentCallbacks)) {
			throw new ClassCastException(
					"Activity must implement PageFragmentCallbacks");
		}

		mCallbacks = (PageFragmentCallbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case CAMERA_REQUEST_CODE:
			if (resultCode == Activity.RESULT_OK) {
				imageView.setImageURI(mNewImageUri);
				writeResult();
			}
			break;
		case GALLERY_REQUEST_CODE:
			if (resultCode == Activity.RESULT_OK && data != null) {
				mNewImageUri = data.getData();
				imageView.setImageURI(mNewImageUri);
				writeResult();
			}
			break;
		}
	}

	private void writeResult() {
		
		mPage.getData().putString(Page.SIMPLE_DATA_KEY,
				(mNewImageUri != null) ? mNewImageUri.toString() : null);
		mPage.notifyDataChanged();
	}

}
