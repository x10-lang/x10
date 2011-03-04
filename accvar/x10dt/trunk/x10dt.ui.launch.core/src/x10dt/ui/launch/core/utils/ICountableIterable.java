/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.core.utils;

/**
 * Improves the {@link Iterable} interface by adding the ability to know the number of elements in the iterable sequence.
 * 
 * @param <T> The type of the elements in the iterable sequence.
 * 
 * @author egeay
 */
public interface ICountableIterable<T> extends Iterable<T> {

  /**
   * Returns the number of elements in the iterable sequence.
   * 
   * @return A natural number.
   */
  public int getSize();
  
}
