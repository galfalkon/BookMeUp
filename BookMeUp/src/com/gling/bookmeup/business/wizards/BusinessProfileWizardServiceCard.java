package com.gling.bookmeup.business.wizards;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardExpand;
import it.gmariotti.cardslib.library.internal.CardHeader;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gling.bookmeup.R;

public class BusinessProfileWizardServiceCard extends Card {
	private final int _price;
	private final int _duration;

	public BusinessProfileWizardServiceCard(Context context, String name,
			int price, int duration, String expand) {
		super(context, R.layout.business_profile_wizard_service_card);
		
		_price = price;
		_duration = duration;
		
		CardHeader cardHeader = new CardHeader(context);
		cardHeader.setTitle(name);
		
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
				.findViewById(R.id.business_profile_wizard_service_price))
				.setText(String.valueOf(_price));

		((TextView) view.findViewById(R.id.business_profile_wizard_service_duration))
				.setText(String.valueOf(_duration));
	}
}
