/*****************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                       *
 * All rights reserved. This program and the accompanying materials          *
 * are made available under the terms of the Eclipse Public License v1.0     *
 * which accompanies this distribution, and is available at                  *
 * http://www.eclipse.org/legal/epl-v10.html                                 *
 *****************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.utils;

/**
 * Defines a functor with one parameter.
 *
 * @param <T1> The functor parameter type.
 * @param <T2> The functor return type.
 *
 * @author egeay
 */
public interface IFunctor<T1, T2> {
  
  /**
   * Returns the element after applying the functor to the element transmitted.
   * 
   * @param element The functor parameter.
   * @return The functor result.
   */
  public T2 apply(final T1 element);

}
