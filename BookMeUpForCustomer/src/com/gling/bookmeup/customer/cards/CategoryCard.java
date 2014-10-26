package com.gling.bookmeup.customer.cards;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.devsmart.android.ui.HorizontalListView;
import com.gling.bookmeup.sharedlib.R;
import com.gling.bookmeup.sharedlib.parse.Business;
import com.gling.bookmeup.sharedlib.parse.ParseHelper.Category;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

public class CategoryCard extends Card {

    private final static int BUSINESS_IMAGE_NUMBER = 10;

    private BusinessImageAdapter _businessImageAdapter;
    private HorizontalListView _listView;

    public CategoryCard(Context context, Category category) {
        super(context, R.layout.customer_category_card_inner_layout);

        CardHeader header = new CardHeader(context);
        header.setTitle(category.getName());
        addCardHeader(header);
        _businessImageAdapter = new BusinessImageAdapter(context, category);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        _listView = (HorizontalListView) view.findViewById(R.id.customer_category_card_list_view);
        _listView.setAdapter(_businessImageAdapter);
        _listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Toast.makeText(getContext(), "open booking wizard or something", Toast.LENGTH_LONG)
                     .show();
            }
        });
    }

    private class BusinessImageAdapter extends ParseQueryAdapter<Business> {

        public BusinessImageAdapter(Context context, final Category category) {
            super(context, new ParseQueryAdapter.QueryFactory<Business>() {
                public ParseQuery<Business> create() {
                    ParseQuery<Business> query = new ParseQuery<Business>(Business.CLASS_NAME);
                    query.whereEqualTo(Business.Keys.CATEGORY, category);
                    query.whereExists(Business.Keys.IMAGE);
                    // TODO order by some "popularity" property
                    query.setLimit(BUSINESS_IMAGE_NUMBER);
                    return query;
                }
            });
        }

        // Customize the layout by overriding getItemView
        @Override
        public View getItemView(Business business, View v, ViewGroup parent) {
            if (v == null) {
                v = View.inflate(getContext(), R.layout.customer_category_card_list_item, null);
            }

            super.getItemView(business, v, parent);

            ParseImageView imageView = (ParseImageView) v
                                                         .findViewById(R.id.customer_category_card_list_item);
            ParseFile imageFile = business.getImageFile();
            imageView.setPlaceholder(getContext().getResources().getDrawable(R.drawable.ic_person));
            imageView.setParseFile(imageFile);
            imageView.loadInBackground();

            return v;
        }

    }
}
