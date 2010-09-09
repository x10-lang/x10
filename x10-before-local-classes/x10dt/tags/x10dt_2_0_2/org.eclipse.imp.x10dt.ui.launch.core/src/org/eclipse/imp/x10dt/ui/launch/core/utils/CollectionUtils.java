/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utility methods for {@link Collection}.
 * 
 * @author egeay
 */
public final class CollectionUtils {
  
  /**
   * Filters elements from a given collection with a particular filter provided and returns a collection with 
   * the (potential) new list of elements.
   * 
   * @param <T> The type of the elements of the original collection.
   * @param source The elements to filter.
   * @param filter The filter to use.
   * @return The filtered collection.
   */
  public static <T> Collection<T> filter( final Collection<T> source, final IFilter<T> filter ) {
    final Collection<T> collection = new ArrayList<T>(source.size());
    for ( final T element : source ) {
      if ( filter.accepts( element ) ) {
        collection.add( element );
      }
    }
    return collection;
  }
  
  /**
   * Converts elements of a collection into a list through a functor with one parameter.
   * 
   * @param <T1> The functor parameter type.
   * @param <T2> The functor return type.
   * @param collection The collection to convert into a list.
   * @param functor The functor to consider
   * @return A list equals to the size of the collection transmitted.
   */
  public static <T1, T2> List<T2> transform(final Collection<T1> collection, final IFunctor<T1, T2> functor) {
    final List<T2> list = new ArrayList<T2>(collection.size());
    for (final T1 element : collection) {
      list.add(functor.apply(element));
    }
    return list;
  }
  
  // --- Private code
  
  private CollectionUtils() {}

}
