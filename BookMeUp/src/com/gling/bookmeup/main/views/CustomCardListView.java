package com.gling.bookmeup.main.views;

import it.gmariotti.cardslib.library.view.CardListView;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.ViewFlipper;

/**
 * A view that wraps a {@link CardListView} that allows showing 'loading' indication. 
 * @author Gal Falkon
 */
public class CustomCardListView extends ViewFlipper {
	private final CardListView _cardListView;
	private final ProgressBar _progressBar;
	
	public CustomCardListView(Context context, AttributeSet attrs) {
		super(context, attrs);

		_progressBar = new ProgressBar(context);
		FrameLayout.LayoutParams progressBarLayoutParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		progressBarLayoutParams.gravity = Gravity.CENTER;
		_progressBar.setLayoutParams(progressBarLayoutParams);
		addView(_progressBar);

		_cardListView = new CardListView(context);
		addView(_cardListView);
		
	}

	public void showLoading()
	{
		showProgressBar();
	}

	
	public void stopLoading()
	{
		showListView();
	}


	public CardListView getCardListView()
	{
		return _cardListView;
	}
	
	private void showProgressBar() 
	{
		setDisplayedChild(0);
	}
	
	private void showListView() {
		setDisplayedChild(1);
	}
}
