package com.gling.bookmeup.customer.cards;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gling.bookmeup.sharedlib.R;
import com.gling.bookmeup.sharedlib.parse.Business;
import com.gling.bookmeup.sharedlib.parse.ParseHelper.Category;
import com.parse.CountCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;

public class CategoryCard extends Card {

    public static final String TAG = "CategoryCard";

    private Category _category;
    private int _numBusinesses;

    public CategoryCard(Context context, Category category) {
        super(context, R.layout.customer_category_card_inner_layout);
        _category = category;
        _numBusinesses = -1;

        CardHeader header = new CardHeader(context,
                R.layout.customer_category_card_header_inner_layout);
        header.setTitle(_category.getName());
        addCardHeader(header);

        CardThumbnail thumb = new CategoryCardThumbnail(getContext());
        ParseFile categoryImage = _category.getImageFile();
        if (categoryImage != null) {
            thumb.setUrlResource(categoryImage.getUrl());
            thumb.setErrorResource(R.drawable.ic_launcher);
        } else {
            thumb.setDrawableResource(R.drawable.ic_launcher);
            thumb.setExternalUsage(false);
        }
        addCardThumbnail(thumb);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, final View view) {
        final TextView numBusinessesTxt = (TextView) view
                                                         .findViewById(R.id.customer_category_card_num_businesses_txt);
        if (_numBusinesses != -1) {
            numBusinessesTxt.setText(String.valueOf(_numBusinesses)
                    + (_numBusinesses == 1 ? " Business" : " Businesses"));
        } else {
            numBusinessesTxt.setText("");
            ParseQuery<Business> query = new ParseQuery<Business>(Business.CLASS_NAME);
            query.whereEqualTo(Business.Keys.CATEGORY, _category);
            query.countInBackground(new CountCallback() {

                @Override
                public void done(int count, ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "failed counting businesses with certain caregory");
                        return;
                    }
                    _numBusinesses = count;
                    numBusinessesTxt.setText(String.valueOf(_numBusinesses)
                            + (_numBusinesses == 1 ? " Business" : " Businesses"));
                }
            });
        }
    }

    class CategoryCardThumbnail extends CardThumbnail {

        public CategoryCardThumbnail(Context context) {
            super(context);
        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View viewImage) {
            if (viewImage != null) {
                if (parent != null && parent.getResources() != null) {
                    DisplayMetrics metrics = parent.getResources().getDisplayMetrics();

                    int base = 98;

                    if (metrics != null) {
                        viewImage.getLayoutParams().width = (int) (base * metrics.density);
                        viewImage.getLayoutParams().height = (int) (base * metrics.density);
                    } else {
                        viewImage.getLayoutParams().width = 196;
                        viewImage.getLayoutParams().height = 196;
                    }
                }
            }

        }
    }

}
