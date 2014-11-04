package com.gling.bookmeup.main;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class Utils {

    public static final String TAG = "Utils";

    private static final int IMAGE_WIDTH = 612;

    public static byte[] getRotatedImage(byte[] image, int deg) {
        if (image == null) {
            return null;
        }

        Bitmap original = BitmapFactory.decodeByteArray(image, 0, image.length);
        Matrix matrix = new Matrix();
        matrix.postRotate(deg);
        Bitmap rotated = Bitmap.createBitmap(original,
                                             0,
                                             0,
                                             original.getWidth(),
                                             original.getHeight(),
                                             matrix,
                                             true);
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        rotated.compress(Bitmap.CompressFormat.PNG, 100, bos); // png is loseless
        return bos.toByteArray();
    }

    public static byte[] getScaledImage(Context context, Uri uri) {
        byte[] data = null;

        try {
            ContentResolver cr = context.getContentResolver();
            InputStream inputStream = cr.openInputStream(uri);
            Bitmap scaledImage = BitmapFactory.decodeStream(inputStream);

            int dstImageWidth = (scaledImage.getWidth() <= IMAGE_WIDTH) ? scaledImage.getWidth()
                    : IMAGE_WIDTH;
            // Resize photo
            scaledImage = Bitmap.createScaledBitmap(scaledImage, dstImageWidth, dstImageWidth
                    * scaledImage.getHeight() / scaledImage.getWidth(), false);

            // Override Android default landscape orientation and save portrait
            // Matrix matrix = new Matrix();
            // matrix.postRotate(90);
            // Bitmap rotatedScaledMealImage =
            // Bitmap.createBitmap(businessImage,
            // 0, 0, businessImage.getWidth(), businessImage.getHeight(),
            // matrix, true);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            scaledImage.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            data = bos.toByteArray();
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }

        return data;
    }
    
    public static void scaleImageToImageView(ImageView view, int boundBoxInDp)
    {
        // Get the ImageView and its bitmap
        Drawable drawing = view.getDrawable();
        Bitmap bitmap = ((BitmapDrawable)drawing).getBitmap();

        // Get current dimensions
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // Determine how much to scale: the dimension requiring less scaling is
        // closer to the its side. This way the image always stays inside your
        // bounding box AND either x/y axis touches it.
        float xScale = ((float) boundBoxInDp) / width;
        float yScale = ((float) boundBoxInDp) / height;
        float scale = (xScale <= yScale) ? xScale : yScale;

        // Create a matrix for the scaling and add the scaling data
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // Create a new bitmap and convert it to a format understood by the ImageView
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        BitmapDrawable result = new BitmapDrawable(scaledBitmap);
        width = scaledBitmap.getWidth();
        height = scaledBitmap.getHeight();

        // Apply the scaled bitmap
        view.setImageDrawable(result);

        // Now change ImageView's dimensions to match the scaled image
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);
    }
}
