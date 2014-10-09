package com.gling.bookmeup.main.views;

import it.gmariotti.cardslib.library.view.CardListView;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.gling.bookmeup.R;

/**
 * A view that wraps a {@link ListView} that allows showing 'loading' and 'no items' indications.
 * The 'no items' text can be customized by setting the {@link R.attr#emptyListText} attribute. If this attributes isn't set,
 * the default text is {@link R.string#default_no_items_in_list_text}
 * @author Gal Falkon
 *
 * @param <T> The type of the list view to be wrapped (e.g. {@link ListView}, {@link CardListView}) 
 */
public abstract class BaseListViewWrapperView<T extends ListView> extends ViewFlipper 
{
	private final T _listView;
	private final ProgressBar _progressBar;
	private final String _noItemsText;
	private final TextView _noItemsView;
	
	public BaseListViewWrapperView(Context context, AttributeSet attrs, T listView) 
	{
		super(context, attrs);

		TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.BaseListViewWrapperView, 0, 0);
		try
		{
			if (attributes.hasValue(R.styleable.BaseListViewWrapperView_emptyListText))
			{
				_noItemsText = attributes.getString(R.styleable.BaseListViewWrapperView_emptyListText);
			}
			else
			{
				_noItemsText = getResources().getString(R.string.default_no_items_in_list_text);
			}
			
		}
		finally
		{
			attributes.recycle();
		}
		
		_listView = listView;
		addView(_listView);
		
		_progressBar = new ProgressBar(context);
		FrameLayout.LayoutParams progressBarLayoutParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		progressBarLayoutParams.gravity = Gravity.CENTER;
		_progressBar.setLayoutParams(progressBarLayoutParams);
		addView(_progressBar);

		LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		_noItemsView = (TextView)layoutInflater.inflate(R.layout.empty_list_view, (ViewGroup) getParent());
		_noItemsView.setText(_noItemsText);
		FrameLayout.LayoutParams noItemsViewLayoutParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		noItemsViewLayoutParams.gravity = Gravity.CENTER;
		_noItemsView.setLayoutParams(noItemsViewLayoutParams);
		addView(_noItemsView);
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas) 
	{
		if (getDisplayedChild() == 0 && _listView.getCount() == 0)
		{
			showNoItems();
		}
		super.dispatchDraw(canvas);
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
	
	private void showNoItems()
	{
		setDisplayedChild(2);
	}
}
