package com.gling.bookmeup.business.wizards.profile;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gling.bookmeup.sharedlib.R;

public class BusinessProfileWizardServiceCard extends Card {
	private final int _price;
	private final int _duration;

	public BusinessProfileWizardServiceCard(Context context, String name,
			int price, int duration, CardHeader.OnClickCardHeaderOtherButtonListener dissmissCallback) {
		super(context, R.layout.business_profile_wizard_service_card);
		
		_price = price;
		_duration = duration;
		
		CardHeader cardHeader = new CardHeader(context);
		cardHeader.setTitle(name);
		
		cardHeader.setOtherButtonDrawable(R.drawable.card_menu_button_other_dismiss);
		cardHeader.setOtherButtonVisible(true);
		// TODO check if null arg is OK
		cardHeader.setOtherButtonClickListener(dissmissCallback);
		
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
