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
 * The {@link ByRef} interface.
 *
 * @param <T>
 *          the object type
 */
public interface ByRef<T extends ByRef<T>> {
  /**
   * Returns the id of the object.
   *
   * @return the id of the object
   */
  GlobalID id();

  /**
   * Returns the object with the given id.
   *
   * @param id
   *          the id of the object
   * @return the object
   */
  T resolve(GlobalID id);
}
