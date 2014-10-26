package com.gling.bookmeup.main;

/**
 * An observer that is being notified whenever a list is being modified.
 * 
 * @author Gal Falkon
 * 
 */
public interface IListChangeObserver
{
	/**
	 * Invoked when an item is added to the list.
	 * 
	 * @param position
	 *            The position of the new item
	 */
	public void onAddItem(int position);

	/**
	 * Invoked when a collection is added to the list.
	 * 
	 * @param fromPosition
	 *            The position where the collection has been inserted at
	 */
	public void onAddAll(int fromPosition);

	/**
	 * Invoked when an item is removed from the list
	 * 
	 * @param position
	 *            The position of the removed item
	 */
	public void onRemoveItem(int position);

	/**
	 * Invoked when the list is cleared
	 */
	public void onClear();
}
