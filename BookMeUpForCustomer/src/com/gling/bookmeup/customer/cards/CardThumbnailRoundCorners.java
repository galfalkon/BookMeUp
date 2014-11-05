package com.gling.bookmeup.customer.cards;

import it.gmariotti.cardslib.library.internal.CardThumbnail;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

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

        bitmap = Utils.scaleImageToDp(bitmap, dpToPx(IMAGE_WIDTH));
        
        LayoutParams params = (LayoutParams) imageView.getLayoutParams();
        params.width = bitmap.getWidth();
        params.height = bitmap.getHeight();
        imageView.setLayoutParams(params);
        
        RoundCornersDrawable round = new RoundCornersDrawable(bitmap, cornerRadius, margin);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            imageView.setBackground(round);
        } else {
            imageView.setBackgroundDrawable(round);
        }
        return true;
    }
    
    private int dpToPx(int dp)
    {
        float density = getContext().getResources().getDisplayMetrics().density;
        return Math.round((float)dp * density);
    }
}
