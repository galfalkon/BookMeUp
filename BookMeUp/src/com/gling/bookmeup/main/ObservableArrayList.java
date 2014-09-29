package com.gling.bookmeup.main;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Observable ArrayList implementation
 * 
 * DISCLAIMER: Currently, only one observer is supported
 * @author Gal Falkon
 *
 * @param <T> The type of elements in the list
 */
public class ObservableArrayList<T> extends ArrayList<T> implements IObservableList<T> {
	private static final long serialVersionUID = 1L;
	
	public IListChangeObserver _observer;
	
	@Override
	public void registerChangeListener(IListChangeObserver changeObserver) {
		_observer = changeObserver;
	}

	@Override
	public void unregisterChangeListener(IListChangeObserver changeObserver) {
		_observer = null;
	}
	
	@Override
	public void add(int index, T object) {
		super.add(index, object);
		_observer.onAddItem(index);
	}
	
	@Override
	public boolean add(T object) {
		boolean ret = super.add(object);
		_observer.onAddItem(size() - 1);
		return ret;
	}
	
	@Override
	public boolean addAll(Collection<? extends T> collection) {
		boolean ret = super.addAll(collection);
		_observer.onAddAll(size() - collection.size());
		return ret;
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends T> collection) {
		boolean ret = super.addAll(index, collection);
		_observer.onAddAll(index);
		return ret;
	}
	
	@Override
	public void clear() {
		super.clear();
		_observer.onClear();
	}
	
	@Override
	public T remove(int index) {
		T ret = super.remove(index);
		_observer.onRemoveItem(index);
		return ret;
	}
	
	@Override
	public boolean remove(Object object) {
		int index = indexOf(object);
		boolean ret = super.remove(object);
		_observer.onRemoveItem(index);
		return ret;
	}
	
	@Override
	public boolean removeAll(Collection<?> collection) {
		boolean ret = true;
		for (Object object : collection)
		{
			ret &= remove(object);
		}
		return ret;
	}
}
