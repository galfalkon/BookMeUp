package com.gling.bookmeup.customer.cards;

import it.gmariotti.cardslib.library.internal.CardThumbnail;

import com.gling.bookmeup.R;
import com.gling.bookmeup.main.drawable.RoundCornersDrawable;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.View;

public class CardThumbnailRoundCorners extends CardThumbnail {

	private static final int CORNER_RADIUS = 12;

	public CardThumbnailRoundCorners(Context context, String url) {
		super(context);

		setUrlResource(url);
		setErrorResource(R.drawable.ic_ic_error_loading);
	}

	@Override
	public boolean applyBitmap(View imageView, Bitmap bitmap) {

		float density = getContext().getResources().getDisplayMetrics().density;
		int cornerRadius = (int) (CORNER_RADIUS * density + 0.5f);
		int margin = 0;

		RoundCornersDrawable round = new RoundCornersDrawable(bitmap,
				cornerRadius, margin);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			imageView.setBackground(round);
		} else {
			imageView.setBackgroundDrawable(round);
		}
		return true;
	}
}
