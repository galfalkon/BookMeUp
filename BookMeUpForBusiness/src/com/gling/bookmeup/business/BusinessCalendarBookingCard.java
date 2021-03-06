package com.gling.bookmeup.business;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardExpand;
import it.gmariotti.cardslib.library.internal.CardHeader;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BusinessCalendarBookingCard extends Card {
	private final String _service, _time, _status;
	private final int _statusColor;

	public BusinessCalendarBookingCard(Context context, String title,
			String service, String time, String status, int statusColor, String expand) {
		super(context, R.layout.business_calendar_booking_card);
		
		_service = service;
		_time = time;
		_status = status;
		_statusColor = statusColor;
		
		CardHeader cardHeader = new CardHeader(context);
		cardHeader.setTitle(title);
		
		if (expand != null) {
			cardHeader.setButtonExpandVisible(true);
			CardExpand cardExpand = new CardExpand(context);
			cardExpand.setTitle(expand);
			addCardExpand(cardExpand);
			
			setOnClickListener(new OnCardClickListener() {
				
				@Override
				public void onClick(Card card, View view) {
					doToogleExpand();
				}
			});
		}
		
		addCardHeader(cardHeader);
	}

	@Override
	public void setupInnerViewElements(ViewGroup parent, View view) {
		((TextView) view
				.findViewById(R.id.business_calendar_booking_card_service))
				.setText(_service);

		((TextView) view.findViewById(R.id.business_calendar_booking_card_time))
				.setText(_time);

		TextView status = (TextView) view
				.findViewById(R.id.business_calendar_booking_card_status);
		status.setTextColor(_statusColor);
		status.setText(_status);
	}
}
