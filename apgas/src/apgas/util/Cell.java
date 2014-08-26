/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package apgas.util;

/**
 * The {@link Cell} class implements a mutable generic container.
 *
 * @param <T>
 *          the type of the contained object
 */
public final class Cell<T> {
  /**
   * The contained object.
   */
  private T t;

  /**
   * Returns the object in this {@link Cell} instance.
   *
   * @return the contained object
   */
  public T get() {
    return t;
  }

  /**
   * Sets the object in this {@link Cell} instance.
   *
   * @param t
   *          an object
   */
  public void set(T t) {
    this.t = t;
  }
}
