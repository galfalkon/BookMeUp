package com.gling.bookmeup.business;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.gling.bookmeup.R;
import com.gling.bookmeup.login.LoginMainActivity;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

public class BusinessImageCaptureFragment extends Fragment {

	public static final String TAG = "BusinessImageCaptureFragment";

	private Camera camera;
	private SurfaceView surfaceView;
	private ParseFile imageFile;
	private ImageButton photoButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.business_image_upload_fragment, parent, false);

		photoButton = (ImageButton) v.findViewById(R.id.camera_photo_button);

		if (camera == null) {
			try {
				camera = Camera.open();
				photoButton.setEnabled(true);
			} catch (Exception e) {
				Log.e(TAG, "No camera with exception: " + e.getMessage());
				photoButton.setEnabled(false);
				Toast.makeText(getActivity(), "No camera detected",
						Toast.LENGTH_LONG).show();
			}
		}

		photoButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (camera == null)
					return;
				camera.takePicture(new Camera.ShutterCallback() {

					@Override
					public void onShutter() {
						// nothing to do
					}

				}, null, new Camera.PictureCallback() {

					@Override
					public void onPictureTaken(byte[] data, Camera camera) {
						saveScaledPhoto(data);
					}

				});

			}
		});

		surfaceView = (SurfaceView) v.findViewById(R.id.camera_surface_view);
		SurfaceHolder holder = surfaceView.getHolder();
		holder.addCallback(new Callback() {

			public void surfaceCreated(SurfaceHolder holder) {
				try {
					if (camera != null) {
						camera.setDisplayOrientation(90);
						camera.setPreviewDisplay(holder);
						camera.startPreview();
					}
				} catch (IOException e) {
					Log.e(TAG, "Error setting up preview", e);
				}
			}

			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				// nothing to do here
			}

			public void surfaceDestroyed(SurfaceHolder holder) {
				// nothing here
			}

		});

		return v;
	}

	/*
	 * ParseQueryAdapter loads ParseFiles into a ParseImageView at whatever size
	 * they are saved. Since we never need a full-size image in our app, we'll
	 * save a scaled one right away.
	 */
	private void saveScaledPhoto(byte[] data) {

		// Resize photo from camera byte array
		Bitmap businessImage = BitmapFactory.decodeByteArray(data, 0, data.length);
		Bitmap businessImageScaled = Bitmap.createScaledBitmap(businessImage, 200, 200
				* businessImage.getHeight() / businessImage.getWidth(), false);

		// Override Android default landscape orientation and save portrait
		Matrix matrix = new Matrix();
		matrix.postRotate(90);
		Bitmap rotatedScaledMealImage = Bitmap.createBitmap(businessImageScaled, 0,
				0, businessImageScaled.getWidth(), businessImageScaled.getHeight(),
				matrix, true);

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		rotatedScaledMealImage.compress(Bitmap.CompressFormat.JPEG, 100, bos);

		byte[] scaledData = bos.toByteArray();

		// Save the scaled image to Parse
		// each ParseFile has a unique identifier separate from the name.
		imageFile = new ParseFile("business_image.jpg", scaledData);
		imageFile.saveInBackground(new SaveCallback() {

			public void done(ParseException e) {
				if (e != null) {
					Toast.makeText(getActivity(),
							"Error saving: " + e.getMessage(),
							Toast.LENGTH_LONG).show();
				} else {
					addPhotoToBusiness(imageFile);
				}
			}
		});
	}

	/*
	 * Once the photo has saved successfully, we're ready to return to the
	 * BusinessProfileCreationFragment. When we added the BusinessImageCaptureFragment to the back stack, 
	 * we named it "BusinessProfileCreationFragment". Now we'll pop fragments off the back stack
	 * until we reach that Fragment.
	 */
	private void addPhotoToBusiness(ParseFile imageFile) {
		//((LoginActivity) getActivity()).getCurrentBusiness().setImageFile(imageFile);
		
		Log.i(TAG, "popping back to profile creation fragment");
		getActivity()
		.getSupportFragmentManager()
		.popBackStack();
//		.popBackStack("BusinessProfileCreationFragment",
//				FragmentManager.POP_BACK_STACK_INCLUSIVE);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (camera == null) {
			try {
				camera = Camera.open();
				photoButton.setEnabled(true);
			} catch (Exception e) {
				Log.i(TAG, "No camera: " + e.getMessage());
				photoButton.setEnabled(false);
				Toast.makeText(getActivity(), "No camera detected",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onPause() {
		if (camera != null) {
			camera.stopPreview();
			camera.release();
		}
		super.onPause();
	}

}