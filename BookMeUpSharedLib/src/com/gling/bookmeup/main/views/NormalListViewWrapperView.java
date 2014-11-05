package com.gling.bookmeup.main.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class NormalListViewWrapperView extends BaseListViewWrapperView<ListView> 
{
	public NormalListViewWrapperView(Context context, AttributeSet attrs) 
	{
		super(context, attrs, new ListView(context));
	}
	
	public void setChoiceMode(int choiceMode)
	{
		_listView.setChoiceMode(choiceMode);
	}
}
