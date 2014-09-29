package com.gling.bookmeup.main;

import java.util.List;

/**
 * The IObservableList interface provides two methods to register and unregister change listeneres.
 * This change listeners will be notified when the list gets changed.
 * @author Gal Falkon
 *
 * @param <T> The type of elements in the list
 */
public interface IObservableList<T> extends List<T> {
	/**
	 * Register a change observer
	 * @param changeObserver The change observer to register
	 */
	public void registerChangeListener(IListChangeObserver changeObserver);
	
	/**
	 * Unregister a change observer
	 * @param changeObserver The change observer to unregister 
	 */
	public void unregisterChangeListener(IListChangeObserver changeObserver);
}
