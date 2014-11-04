package com.gling.bookmeup.customer.cards;

import it.gmariotti.cardslib.library.internal.CardThumbnail;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;

import com.gling.bookmeup.main.Utils;
import com.gling.bookmeup.main.drawable.RoundCornersDrawable;
import com.gling.bookmeup.sharedlib.R;

public class CardThumbnailRoundCorners extends CardThumbnail {

    private static final int CORNER_RADIUS = 12;
    private static final int IMAGE_WIDTH = 70;

    public CardThumbnailRoundCorners(Context context, String url) {
        super(context);
        setUrlResource(url);
        setErrorResource(R.drawable.ic_error_loadingorangesmall);
    }

    @Override
    public boolean applyBitmap(View imageView, Bitmap bitmap) {

        float density = getContext().getResources().getDisplayMetrics().density;
        int cornerRadius = (int) (CORNER_RADIUS * density + 0.5f);
        int margin = 0;

        // Bitmap scaledImage = Bitmap.createScaledBitmap(bitmap, IMAGE_WIDTH,
        // IMAGE_WIDTH
        // * bitmap.getHeight() / bitmap.getWidth(), false);
        RoundCornersDrawable round = new RoundCornersDrawable(bitmap, cornerRadius, margin);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            imageView.setBackground(round);
        } else {
            imageView.setBackgroundDrawable(round);
        }
        //Utils.scaleImageToImageView((ImageView) imageView, IMAGE_WIDTH);
        return true;
    }
}
