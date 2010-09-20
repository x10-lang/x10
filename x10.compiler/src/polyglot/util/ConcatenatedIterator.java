/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

/*
 * ConcatenatedIterator.java
 */

package polyglot.util;

import java.util.Iterator;

/**
 * ConcatenatedIterator
 *
 * Overview:
 *     This iterator wraps other iterators, and returns all their elements
 *     in order.
 *
 *     Does not support Remove.
 **/
public final class ConcatenatedIterator<T> implements Iterator<T> {
  /**
   * Constructs a new ConcatenatedIterator which yields all of the
   * elements of <iter1>, followed by all the elements of <iter2>.
   **/
  @SuppressWarnings("unchecked") // Generics and arrays don't mix well
  public ConcatenatedIterator(Iterator<? extends T> iter1, Iterator<? extends T> iter2) {
    this(new Iterator[]{iter1, iter2});
  }

  /**
   * Constructs a new ConcatenatedIterator which yields every element, in
   *  order, of every element of the array iters, in order.
   **/
  public ConcatenatedIterator(Iterator<? extends T>[] iters) {
    this.backing_iterators = (Iterator<? extends T>[]) iters.clone();
    findNextItem();
  }

  /**
   * Constructs a new ConcatenatedIterator which yields every element, in
   * order, of every element of the collection iters, in order.
   **/
  @SuppressWarnings("unchecked") // Generics and arrays don't mix well
  public ConcatenatedIterator(java.util.Collection<Iterator<? extends T>> iters) {
    this.backing_iterators = (Iterator<? extends T>[])iters.toArray(new Iterator[0]);
    findNextItem();
  }

  public T next() {
    T res = next_item;
    if (res == null)
      throw new java.util.NoSuchElementException();
    findNextItem();
    return res;
  }

  public boolean hasNext() {
    return next_item != null;
  }
  
  public void remove() {
    throw new UnsupportedOperationException("ConcatenatedIterator.remove");
  }

  // Advances the internal iterator.
  private void findNextItem() {
    while(index < backing_iterators.length) {
      Iterator<? extends T> it = backing_iterators[index];
      if (it.hasNext()) {
	next_item = it.next();
	return;
      } else {
	index++;
      }
    }
    next_item = null;
  }
  
  // AF:  if next_item==null, this iterator has no more elts to yield.
  //      otherwise, this iterator will yield next_item, followed by the 
  //      remaining elements of backing_iterators[index], followed by the
  //      elements of backing_iterators[index+1]...
  protected T next_item;
  protected Iterator<? extends T>[] backing_iterators;
  protected int index;
}


