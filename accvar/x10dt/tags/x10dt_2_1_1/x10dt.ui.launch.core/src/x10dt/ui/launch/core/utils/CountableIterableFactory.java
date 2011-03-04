/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.core.utils;

import java.util.Collection;

/**
 * Factory methods to create implementations of {@link ICountableIterable} interface.
 * 
 * @author egeay
 */
public final class CountableIterableFactory {
  
  /**
   * Creates a countable iterable instance for the unique collection provided.
   * 
   * @param <T> The type of the collection items.
   * @param c1 The unique collection.
   * @return A non-null implementation of {@link ICountableIterable}.
   */
  public static <T> ICountableIterable<T> create(final Collection<T> c1) {
    return new CountableIterable<T>(c1);
  }
  
  /**
   * Creates a countable iterable instance for the two collections provided.
   * 
   * @param <T> The type of the collection items.
   * @param c1 The first collection.
   * @param c2 The second collection.
   * @return A non-null implementation of {@link ICountableIterable}.
   */
  @SuppressWarnings("unchecked")
  public static <T> ICountableIterable<T> create(final Collection<T> c1, final Collection<T> c2) {
    return new CountableIterableComposite<T>(c1, c2);
  }

  /**
   * Creates a countable iterable instance for the three collections provided.
   * 
   * @param <T> The type of the collection items.
   * @param c1 The first collection.
   * @param c2 The second collection.
   * @param c3 The third collection.
   * @return A non-null implementation of {@link ICountableIterable}.
   */
  @SuppressWarnings("unchecked")
  public static <T> ICountableIterable<T> create(final Collection<T> c1, final Collection<T> c2, final Collection<T> c3) {
    return new CountableIterableComposite<T>(c1, c2, c3);
  }
  
  /**
   * Creates a countable iterable instance for the four collections provided.
   * 
   * @param <T> The type of the collection items.
   * @param c1 The first collection.
   * @param c2 The second collection.
   * @param c3 The third collection.
   * @param c4 The fourth collection.
   * @return A non-null implementation of {@link ICountableIterable}.
   */
  @SuppressWarnings("unchecked")
  public static <T> ICountableIterable<T> create(final Collection<T> c1, final Collection<T> c2, final Collection<T> c3,
                                                 final Collection<T> c4) {
    return new CountableIterableComposite<T>(c1, c2, c3, c4);
  }
  
  // --- Private code
  
  private CountableIterableFactory() {}

}
