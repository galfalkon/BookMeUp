package com.gling.bookmeup.main;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayMultiChoiceAdapter;
import it.gmariotti.cardslib.library.view.CardView;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.gling.bookmeup.sharedlib.R;

public abstract class GenericMultiChoiceCardArrayAdapter<T> extends CardArrayMultiChoiceAdapter
{
	private static final String TAG = "GenericMultiChoiceCardArrayAdapter";
	
	private final Activity _activity;
	private final IObservableList<T> _items;
	private final List<Card> _cards;
	private final ICardGenerator<T> _cardFactory;
	private final int _menuRes;
	
	public GenericMultiChoiceCardArrayAdapter(Activity activity, IObservableList<T> items, ICardGenerator<T> cardFactory, int menuRes) 
	{
		this(activity, new ArrayList<Card>(), items, cardFactory, menuRes);
	}
	
	private GenericMultiChoiceCardArrayAdapter(Activity activity, List<Card> cards, IObservableList<T> items, ICardGenerator<T> cardFactory, int menuRes) 
	{
		super(activity, cards);
		_activity = activity;
		_items = items;
		_cards = cards;
		_items.registerChangeListener(new ItemListListener());
		_cardFactory = cardFactory;
		_menuRes = menuRes;
	}

	public void refreshItem(int position)
	{
		_cards.remove(position);
		_cards.add(_cardFactory.generateCard(_items.get(position)));
	}
	
	@Override
	public void remove(Card object) {
		_items.remove(_cards.indexOf(object));
	}
	
	@Override
    public void onItemCheckedStateChanged(final ActionMode mode, int position, long id, boolean checked, CardView cardView, Card card) 
	{
    	Log.i(TAG, "Click;" + position + " - " + checked);
    	
    	LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	View view = layoutInflater.inflate(R.layout.contextual_action_bar_title, null);
    	
    	TextView txtNumItems = (TextView)view.findViewById(R.id.contextual_action_bar_txtNumItems);
    	txtNumItems.setText(getCardListView().getCheckedItemCount() + "\nSelected");
    	
    	Button btnSelectAll = (Button)view.findViewById(R.id.contextual_action_bar_btnSelectAll);
    	btnSelectAll.setOnClickListener(new OnClickListener() 
    	{
			@Override
			public void onClick(View view) 
			{
				ListView listView = getCardListView();
				for (int i = 0; i < _cards.size(); i++)
				{
					listView.setItemChecked(i, true);
				}
			}
		});
    	mode.setCustomView(view);
    }

	@Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) 
	{
        return false;
    }
	
	@Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) 
	{
        super.onCreateActionMode(mode, menu);

        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(_menuRes, menu);

        return true;
    }
	
	public List<T> getSelectedItems()
	{
		List<T> selectedItems = new ArrayList<T>();
		List<Card> selectedCards = getSelectedCards();
		for (Card card : selectedCards)
		{
			selectedItems.add(_items.get(_cards.indexOf(card)));
		}
		return selectedItems;
	}
	
	private class ItemListListener implements IListChangeObserver
	{
		@Override
		public void onAddItem(int position) {
			_cards.add(position, _cardFactory.generateCard(_items.get(position)));
			_activity.runOnUiThread(new Runnable() 
			{
				@Override
				public void run() 
				{
					notifyDataSetChanged();
				}
			});
		}

		@Override
		public void onAddAll(final int fromPosition) {
			new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... params) {
					Log.i(TAG, "doInBackground");
					for (int i = fromPosition; i < _items.size(); i++)
					{
						_cards.add(_cardFactory.generateCard(_items.get(i)));
						publishProgress();
					}
					
					return null;
				}
				
				@Override
				protected void onProgressUpdate(Void... values) {
					// TODO: Show an indication to the user that more cards are being loaded
					Log.i(TAG, "onProgressUpdate");
					super.onProgressUpdate(values);
					notifyDataSetChanged();
				}
			}.execute();
		}

		@Override
		public void onRemoveItem(int position) {
			_cards.remove(position);
			_activity.runOnUiThread(new Runnable() 
			{
				@Override
				public void run() 
				{
					notifyDataSetChanged();
				}
			});
		}

		@Override
		public void onClear() {
			_cards.clear();
			_activity.runOnUiThread(new Runnable() 
			{
				@Override
				public void run() 
				{
					notifyDataSetChanged();
				}
			});
		}
	}
}