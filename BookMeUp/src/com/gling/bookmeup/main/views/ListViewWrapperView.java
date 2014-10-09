package com.gling.bookmeup.main.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * A view that wraps a {@link ListView} that allows showing 'loading' indication. 
 * @author Gal Falkon
 */
public class ListViewWrapperView extends BaseListViewWrapperView<ListView> {
	public ListViewWrapperView(Context context, AttributeSet attrs) {
		super(context, attrs, new ListView(context));
	}
}
