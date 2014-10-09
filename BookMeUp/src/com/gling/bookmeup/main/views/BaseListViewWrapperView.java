package com.gling.bookmeup.main.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ViewFlipper;

/**
 * A view that wraps a {@link ListView} that allows showing 'loading' indication.
 * @author Gal Falkon
 *
 * @param <T> The type of the list view to be wrapped (e.g. {@link ListView}, {@link CardListView}) 
 */
public abstract class BaseListViewWrapperView<T extends ListView> extends ViewFlipper {
	private final T _listView;
	private final ProgressBar _progressBar;
	
	public BaseListViewWrapperView(Context context, AttributeSet attrs, T listView) {
		super(context, attrs);

		_listView = listView;
		addView(_listView);
		
		_progressBar = new ProgressBar(context);
		FrameLayout.LayoutParams progressBarLayoutParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		progressBarLayoutParams.gravity = Gravity.CENTER;
		_progressBar.setLayoutParams(progressBarLayoutParams);
		addView(_progressBar);
	}

	public void showLoading()
	{
		showProgressBar();
	}

	public void stopLoading()
	{
		showListView();
	}


	public T getListView()
	{
		return _listView;
	}

	private void showListView() {
		setDisplayedChild(0);
	}
	
	private void showProgressBar() 
	{
		setDisplayedChild(1);
	}
}
