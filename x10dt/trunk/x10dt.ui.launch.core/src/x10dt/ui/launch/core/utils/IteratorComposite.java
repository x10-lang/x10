/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.core.utils;

import java.util.Iterator;

/**
 * Implements an aggregation of iterators seen as one iterator instance for Java collections framework.
 * 
 * @param <T> The type of the elements in the iterable sequence.
 * 
 * @author egeay
 */
public final class IteratorComposite<T> implements Iterator<T> {

  /**
   * Creates the aggregation from the list of iterators provided.
   * 
   * @param iterators The list of iterators to aggregate.
   */
  public IteratorComposite(final Iterator<T> ... iterators) {
    this.fIterators = iterators;
  }
  
  // --- Interface methods implementation
  
  public boolean hasNext() {
    for (final Iterator<T> it : this.fIterators) {
      if (it.hasNext()) {
        return true;
      }
    }
    return false;
  }

  public T next() {
    for (final Iterator<T> it : this.fIterators) {
      if (it.hasNext()) {
        return it.next();
      }
    }
    return null;
  }

  public void remove() {
    throw new UnsupportedOperationException();
  }
  
  // --- Fields
  
  private final Iterator<T>[] fIterators;

}
