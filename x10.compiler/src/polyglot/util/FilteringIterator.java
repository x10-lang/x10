/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2015.
 */

package polyglot.util;

import java.util.Iterator;
import java.util.Collection;

/**
 * FilteringIterator
 *
 * Overview:
 *     This iterator wraps another iterator, and returns only those elements
 *     for which a given predicate is true.  
 *
 *     Does not support Remove.
 **/
public final class FilteringIterator<T> implements Iterator<T> {
  /**
   * Constructs a new FilteringIterator which returns only those elements of
   * <coll> which have <pred> true.
   **/
  public FilteringIterator(Collection<? extends T> iterator, Predicate<? super T> keyNotInMyMap) {
    this(iterator.iterator(), keyNotInMyMap);
  }

  /**
   * Constructs a new FilteringIterator which returns all the elements
   * of <iter>, in order, only when they have <pred> true.
   **/
  public FilteringIterator(Iterator<? extends T> iter, Predicate<? super T> pred) {
    backing_iterator = iter;
    predicate = pred;
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
    throw new UnsupportedOperationException("FilteringIterator.remove");
  }

  // Advances the internal iterator.
  private void findNextItem() {
    while (backing_iterator.hasNext()) {
      T o = backing_iterator.next();
      if (predicate.isTrue(o)) {
	next_item = o;
	return;
      }
    }
    next_item = null;
  }
  
  // AF:  if next_item==null, this iterator has no more elts to yield.
  //      otherwise, this iterator will yield next_item, followed by
  //      those elements e of backing_iterator such that predicate.isTrue(e).
  protected T next_item;
  protected Iterator<? extends T> backing_iterator;
  protected Predicate<? super T> predicate;
}


