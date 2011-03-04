/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.core.utils;

import java.util.Collection;
import java.util.Iterator;


final class CountableIterable<T> implements ICountableIterable<T> {

  CountableIterable(final Collection<T> collection) {
    this.fCollection = collection;
  }
  
  public Iterator<T> iterator() {
    return this.fCollection.iterator();
  }

  public int getSize() {
    return this.fCollection.size();
  }
  
  // --- Fields
  
  private final Collection<T> fCollection;

}
