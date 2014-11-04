package com.gling.bookmeup.customer.cards;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.gling.bookmeup.main.Constants;
import com.gling.bookmeup.sharedlib.R;
import com.gling.bookmeup.sharedlib.parse.Business;
import com.gling.bookmeup.sharedlib.parse.Customer;
import com.gling.bookmeup.sharedlib.parse.ParseHelper.Booking;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class BusinessCard extends Card {

    public static final String TAG = "BusinessCard";

    private Business _business;

    public BusinessCard(Context context, Business business) {
        super(context, R.layout.customer_business_card_inner_layout);
        _business = business;
        
        if (business.getImageFile() != null) {
            CardThumbnailRoundCorners thumb = new CardThumbnailRoundCorners(context,
                    business.getImageFile().getUrl());
            addCardThumbnail(thumb);
        }
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, final View view) {
        final TextView businessName = (TextView) view
                                                     .findViewById(R.id.customer_business_card_business_name);
        businessName.setText(_business.getName());

        final ImageView businessCall = (ImageView) view
                                                       .findViewById(R.id.customer_business_card_business_call);
        businessCall.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i(TAG, "call business button clicked");
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + _business.getPhoneNumber()));
                getContext().startActivity(callIntent);
            }
        });

        final TextView lastVisited = (TextView) view
                                                    .findViewById(R.id.customer_business_card_last_visited);

        ParseQuery<Booking> query = new ParseQuery<Booking>(Booking.CLASS_NAME);
        query.whereEqualTo(Booking.Keys.BUSINESS_POINTER, _business);
        query.whereEqualTo(Booking.Keys.CUSTOMER_POINTER, Customer.getCurrentCustomer());
        query.orderByDescending(Booking.Keys.DATE);
        query.getFirstInBackground(new GetCallback<Booking>() {

            @Override
            public void done(Booking booking, ParseException e) {
                if (e == null) {
                    lastVisited.setText(Constants.DATE_FORMAT.format(booking.getDate()));
                } else {
                    if (e.getCode() == ParseException.OBJECT_NOT_FOUND) { // no
                                                                          // results
                                                                          // found
                                                                          // for
                                                                          // query
                        lastVisited.setText("Never");
                    } else {
                        Log.e(TAG, e.getMessage());
                        Crouton.showText((Activity) getContext(),
                                         R.string.generic_exception_message,
                                         Style.ALERT);
                    }
                }
            }
        });
    }
}
