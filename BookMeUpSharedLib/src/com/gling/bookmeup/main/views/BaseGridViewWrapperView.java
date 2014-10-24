package com.gling.bookmeup.main.views;

import it.gmariotti.cardslib.library.internal.Card;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.gling.bookmeupsharedlib.R;

public abstract class BaseGridViewWrapperView<T extends GridView> extends ViewFlipper 
{
	private static final String TAG = "BaseGridViewWrapperView";
	
	public static enum DisplayMode 
	{
		LIST_VIEW, LOADING_VIEW, NO_ITEMS_VIEW;
	}

	protected final T _gridView;
	private final ProgressBar _progressBar;
	private final String _noItemsText;
	private final TextView _noItemsView;
	
	public BaseGridViewWrapperView(Context context, AttributeSet attrs, T gridView) 
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

//			if (attributes.hasValue(R.styleable.card_options_list_card_layout_resourceID))
//			{
//				gridView.add
////				_gridView.set_noItemsText = attributes.getString(R.styleable.card_options_list_card_layout_resourceID);
//			}
		}
		finally
		{
			attributes.recycle();
		}
		
		_gridView = gridView;
		addView(_gridView);
		
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
	
	public void setAdapter(ArrayAdapter<Card> arrayAdapter)
	{
		Log.i(TAG, "setAdapter");
		_gridView.setAdapter(arrayAdapter);
	}
	
	public DisplayMode getDisplayMode()
	{
		switch (getDisplayedChild())
		{
		case 0:
			return DisplayMode.LIST_VIEW;
		case 1:
			return DisplayMode.LOADING_VIEW;
		case 2:
		default:
			return DisplayMode.NO_ITEMS_VIEW;
		}
	}
	
	public void setDisplayMode(DisplayMode mode)
	{
		Log.i(TAG, String.format("setDisplayMode(%s)", mode.toString()));
		
		if (mode == getDisplayMode())
		{
			return;
		}
		
		switch (mode)
		{
		case LIST_VIEW:
			setDisplayedChild(0);
			break;
		case LOADING_VIEW:
			setDisplayedChild(1);
			break;
		case NO_ITEMS_VIEW:
			setDisplayedChild(2);
			break;
		}
	}
}
