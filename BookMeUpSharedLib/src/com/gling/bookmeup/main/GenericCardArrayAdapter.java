package com.gling.bookmeup.main;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Array adapter for classes that extends ICardViewable (i.e. Supports generation of Card instance)
 * @author Gal Falkon
 *
 * @param <T> The type of elements in the array adapter
 */
public class GenericCardArrayAdapter<T> extends CardArrayAdapter
{
	private final Activity _activity;
	private final IObservableList<T> _items;
	private final List<Card> _cards;
	private final ICardGenerator<T> _cardFactory;
	
	public GenericCardArrayAdapter(Activity activity, IObservableList<T> items, ICardGenerator<T> cardFactory) 
	{
		this(activity, new ArrayList<Card>(), items, cardFactory);
	}
	
	private GenericCardArrayAdapter(Activity activity, List<Card> cards, IObservableList<T> items, ICardGenerator<T> cardFactory) 
	{
		super(activity, cards);
		_activity = activity;
		_items = items;
		_cards = cards;
		_items.registerChangeListener(new ItemListListener());
		_cardFactory = cardFactory;
	}
	
	@Override
	public void remove(Card object) {
		_items.remove(_cards.indexOf(object));
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