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
 *  (C) Copyright IBM Corporation 2007-2014.
 */

package polyglot.util;

import java.util.Iterator;
import java.util.Collection;

/**
 * TransformingIterator
 *
 * Overview:
 *     This is a swiss-army-knife of iterators.  It concatenates, maps, and
 *     filters.  
 *
 *     Does not support Remove.
 **/
public final class TransformingIterator<S,T> implements Iterator<T> {
  @SuppressWarnings("unchecked") // Generics and arrays don't mix well
  public TransformingIterator(Iterator<S> iter, Transformation<S,T> trans) {
    this(new Iterator[]{iter}, trans);
  }

  @SuppressWarnings("unchecked") // Generics and arrays don't mix well
  public TransformingIterator(Collection<S> iters, Transformation<S,T> trans) {
    index = 0;
    backing_iterators = (Iterator<S>[]) iters.toArray(new Iterator[0]);
    transformation = trans;
    if (backing_iterators.length > 0)
      current_iter = backing_iterators[0];
    findNextItem();
  }

  public TransformingIterator(Iterator<S>[] iters, Transformation<S,T> trans) {
    index = 0;
    backing_iterators = (Iterator<S>[]) iters.clone();
    transformation = trans;
    if (iters.length > 0) 
      current_iter = iters[0];
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
    throw new UnsupportedOperationException("TransformingIterator.remove");
  }

  // Advances the internal iterator.
  private void findNextItem() {
    while (current_iter != null) {
    inner_loop:
      while (current_iter.hasNext()) {		
	S o = current_iter.next();	
	T res = transformation.transform(o);
	if (res == null)
	  continue inner_loop;
	next_item = res;
	return;
      }
      index++;
      if (index < backing_iterators.length) {
	current_iter = (Iterator<S>) backing_iterators[index];
      } else {
	current_iter = null;
      }
    }
    next_item = null;
  }
  
  // AF:  if next_item==null, this iterator has no more elts to yield.
  //      otherwise, this iterator will yield next_item, followed by
  //      those elements e of backing_iterator[index] transformed by TRANS.
  // RI: current_iter = backing_iterators[index], or null if no 
  //     backing_iterator hasNext.
  protected T next_item;
  protected Iterator<S> current_iter;
  protected int index;
  protected Iterator<S>[] backing_iterators;
  protected Transformation<S,T> transformation;
}


