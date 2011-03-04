/*****************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                       *
 * All rights reserved. This program and the accompanying materials          *
 * are made available under the terms of the Eclipse Public License v1.0     *
 * which accompanies this distribution, and is available at                  *
 * http://www.eclipse.org/legal/epl-v10.html                                 *
 *****************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.utils.collections;

/**
 * Identity function for a functor with one parameter.
 * 
 * @param <T> The functor parameter and return type.
 *
 * @author egeay
 */
public final class IdentityFunctor<T> implements IFunctor<T, T> {

  // --- Interface methods implementation
  
  public T apply(final T element) {
    return element;
  }

}
