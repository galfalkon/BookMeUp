package com.gling.bookmeup.customer;

import it.gmariotti.cardslib.library.internal.Card;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BookingCard extends Card 
{
	private final String _title, _status;
	private final int _statusColor;
	
	public BookingCard(Context context, String title, String status, int statusColor) 
	{
		super(context, R.layout.customer_my_bookings_booking_card);
		_title = title;
		_status = status;
		_statusColor = statusColor;
	}

	@Override
	public void setupInnerViewElements(ViewGroup parent, View view) 
	{
		TextView txtTitle = (TextView) view.findViewById(R.id.customer_my_bookings_booking_card_title);
		txtTitle.setText(_title);
		
		TextView txtStatus = (TextView) view.findViewById(R.id.customer_my_bookings_booking_card_status);
		txtStatus.setTextColor(_statusColor);
		txtStatus.setText(_status);
	}
}
