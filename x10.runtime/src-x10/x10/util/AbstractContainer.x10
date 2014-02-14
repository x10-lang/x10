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

package x10.util;

/** A mutable or immutable collection. */
public abstract class AbstractContainer[T] implements Container[T] {
  public abstract def size(): Long;
  
  public def isEmpty(): Boolean = size() == 0;
  
  public abstract def contains(y: T): Boolean;
  public abstract def clone(): Container[T];
  public abstract def iterator(): Iterator[T];
  
  public def containsAll(c: Container[T]): Boolean {
      for (x: T in c) {
          if (! contains(x))
              return false;
      }
      return true;
  }
}
