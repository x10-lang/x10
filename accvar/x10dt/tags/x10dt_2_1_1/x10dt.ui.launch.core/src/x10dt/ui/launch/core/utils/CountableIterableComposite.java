/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.core.utils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;


final class CountableIterableComposite<T> implements ICountableIterable<T> {
  
  CountableIterableComposite(final Collection<T> ... collections) {
    int size = 0;
    for (final Collection<T> collection : collections) {
      size += collection.size();
    }
    this.fCollections = collections;
    this.fSize = size;
  }
    
  // --- Interface methods implementation
  
  public Iterator<T> iterator() {
    @SuppressWarnings("unchecked")
    final Iterator<T>[] iterators = (Iterator<T>[]) Array.newInstance(Iterator.class, this.fCollections.length);
    int i = -1;
    for (final Collection<T> collection : this.fCollections) {
      iterators[++i] = collection.iterator();
    }
    return new IteratorComposite<T>(iterators);
  }

  public int getSize() {
    return this.fSize;
  }
  
  // --- Fields
  
  private final Collection<T>[] fCollections;
  
  private final int fSize;

}
