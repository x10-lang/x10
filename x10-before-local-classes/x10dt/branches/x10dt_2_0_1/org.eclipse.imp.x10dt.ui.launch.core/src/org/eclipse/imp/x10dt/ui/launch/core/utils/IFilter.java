/*****************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                       *
 * All rights reserved. This program and the accompanying materials          *
 * are made available under the terms of the Eclipse Public License v1.0     *
 * which accompanies this distribution, and is available at                  *
 * http://www.eclipse.org/legal/epl-v10.html                                 *
 *****************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.utils;

/**
 * Defines a filter on data structures with one dimension.
 * 
 * @param <T> The type of the elements to filter.
 * 
 * @author egeay
 */
public interface IFilter<T> {
  
  /**
   * Returns a boolean indicating if the element transmitted is filtered or not.
   * 
   * @param element The element to consider.
   * @return True if we don't want to filter it, false otherwise.
   */
  public boolean accepts(final T element);

}
