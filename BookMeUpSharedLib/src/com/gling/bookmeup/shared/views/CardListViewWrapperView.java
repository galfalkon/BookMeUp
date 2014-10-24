package com.gling.bookmeup.shared.views;

import it.gmariotti.cardslib.library.view.CardListView;
import android.content.Context;
import android.util.AttributeSet;

/**
 * A view that wraps a {@link CardListView} that allows showing 'loading' indication. 
 * @author Gal Falkon
 */
public class CardListViewWrapperView extends BaseListViewWrapperView<CardListView> 
{
	public CardListViewWrapperView(Context context, AttributeSet attrs) 
	{
		super(context, attrs, new CardListView(context));
	}
	
	public void setChoiceMode(int choiceMode)
	{
		_listView.setChoiceMode(choiceMode);
	}
}
