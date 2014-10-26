package com.gling.bookmeup.main;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

public class Utils
{

	public static final String TAG = "Utils";

	private static final int IMAGE_WIDTH = 612;

	public static byte[] getScaledImage(Context context, Uri uri)
	{
		byte[] data = null;

		try
		{
			ContentResolver cr = context.getContentResolver();
			InputStream inputStream = cr.openInputStream(uri);
			Bitmap businessImage = BitmapFactory.decodeStream(inputStream);

			// Resize photo
			businessImage = Bitmap.createScaledBitmap(businessImage,
					IMAGE_WIDTH, IMAGE_WIDTH * businessImage.getHeight()
							/ businessImage.getWidth(), false);

			// Override Android default landscape orientation and save portrait
			// Matrix matrix = new Matrix();
			// matrix.postRotate(90);
			// Bitmap rotatedScaledMealImage =
			// Bitmap.createBitmap(businessImage,
			// 0, 0, businessImage.getWidth(), businessImage.getHeight(),
			// matrix, true);

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			businessImage.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			data = bos.toByteArray();
		} catch (FileNotFoundException e)
		{
			Log.e(TAG, e.getMessage());
		}

		return data;
	}
}
