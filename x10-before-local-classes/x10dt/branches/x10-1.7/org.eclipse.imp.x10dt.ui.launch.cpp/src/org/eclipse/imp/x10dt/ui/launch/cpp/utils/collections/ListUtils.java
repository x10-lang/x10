/*****************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                       *
 * All rights reserved. This program and the accompanying materials          *
 * are made available under the terms of the Eclipse Public License v1.0     *
 * which accompanies this distribution, and is available at                  *
 * http://www.eclipse.org/legal/epl-v10.html                                 *
 *****************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.utils.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utility methods around {@link List} data structure.
 *
 * @author egeay
 */
public final class ListUtils {
  
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

}
