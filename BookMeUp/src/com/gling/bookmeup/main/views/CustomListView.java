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
 */
public class CustomListView extends ViewFlipper {
	private final ListView _listView;
	private final ProgressBar _progressBar;
	
	public CustomListView(Context context, AttributeSet attrs) {
		super(context, attrs);

		_listView = new ListView(context);
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


	public ListView getListView()
	{
		return _listView;
	}
	
	private void showProgressBar() 
	{
		setDisplayedChild(0);
	}
	
	private void showListView() {
		setDisplayedChild(1);
	}
}
