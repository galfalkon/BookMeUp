package com.gling.bookmeup.main;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import android.content.Context;
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
	private final IObservableList<T> _items;
	private final List<Card> _cards;
	private final ICardGenerator<T> _cardFactory;
	
	public GenericCardArrayAdapter(Context context, IObservableList<T> items, ICardGenerator<T> cardFactory) 
	{
		this(context, new ArrayList<Card>(), items, cardFactory);
	}
	
	private GenericCardArrayAdapter(Context context, List<Card> cards, IObservableList<T> items, ICardGenerator<T> cardFactory) 
	{
		super(context, cards);
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
			_cards.add(_cardFactory.generateCard(_items.get(position)));
			notifyDataSetChanged();
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
			notifyDataSetChanged();
		}

		@Override
		public void onClear() {
			_cards.clear();
			notifyDataSetChanged();
		}
	}
}