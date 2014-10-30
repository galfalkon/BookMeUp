package com.gling.bookmeup.customer.cards;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gling.bookmeup.sharedlib.R;
import com.gling.bookmeup.sharedlib.parse.Business;
import com.gling.bookmeup.sharedlib.parse.ParseHelper.Category;
import com.parse.CountCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

public class CategoryCard extends Card {

    public static final String TAG = "CategoryCard";
    
    private Category _category;
    
    public CategoryCard(Context context, Category category) {
        super(context, R.layout.customer_category_card_inner_layout);
        _category = category;

        CardHeader header = new CardHeader(context,
                R.layout.customer_category_card_header_inner_layout);
        header.setTitle(_category.getName());
        addCardHeader(header);

        CardThumbnail thumb = new CardThumbnail(getContext());
        thumb.setDrawableResource(R.drawable.ic_launcher);
        addCardThumbnail(thumb);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, final View view) {
        ParseQuery<Business> query = new ParseQuery<Business>(Business.CLASS_NAME);
        query.whereEqualTo(Business.Keys.CATEGORY, _category);
        query.countInBackground(new CountCallback() {
            
            @Override
            public void done(int count, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "failed counting businesses with certain caregory");
                    return;
                }
                
                if (view != null) {
                    TextView numBusinessesTxt = (TextView) view
                            .findViewById(R.id.customer_category_card_num_businesses_txt);
                    
                    if (numBusinessesTxt != null) {
                        numBusinessesTxt.setText(String.valueOf(count) + (count == 1 ? " Business" : " Businesses"));
                    }
                }
            }
        });
        
        
    }

}
