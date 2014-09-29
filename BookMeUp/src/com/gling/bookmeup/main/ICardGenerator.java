package com.gling.bookmeup.main;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * A factory for generating Card instances out of the defined type instances  
 * @author Gal Falkon
 *
 * @param <T> The type of elements this factory generates cards from
 */
public interface ICardGenerator<T> {
	public Card generateCard(T object);
}