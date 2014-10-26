package com.gling.bookmeup.main.views;

import it.gmariotti.cardslib.library.view.CardGridView;
import android.content.Context;
import android.util.AttributeSet;

public class CardGridViewWrapperView extends
		BaseGridViewWrapperView<CardGridView>
{
	public CardGridViewWrapperView(Context context, AttributeSet attrs)
	{
		super(context, attrs, new CardGridView(context, attrs));
	}

	public void setChoiceMode(int choiceMode)
	{
		_gridView.setChoiceMode(choiceMode);
	}
}
