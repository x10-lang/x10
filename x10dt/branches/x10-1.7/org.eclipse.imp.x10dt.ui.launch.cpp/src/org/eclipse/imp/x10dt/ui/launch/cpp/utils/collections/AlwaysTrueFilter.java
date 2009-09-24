/*****************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                       *
 * All rights reserved. This program and the accompanying materials          *
 * are made available under the terms of the Eclipse Public License v1.0     *
 * which accompanies this distribution, and is available at                  *
 * http://www.eclipse.org/legal/epl-v10.html                                 *
 *****************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.utils.collections;

/**
 * A filter that always accepts all elements.
 *
 * @param <T> The type of the elements to filter.
 *
 * @author egeay
 */
public final class AlwaysTrueFilter<T> implements IFilter<T>{

  // --- Interface methods implementation
  
  public boolean accepts(final T element) {
    return true;
  }

}
