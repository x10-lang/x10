/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.util;

/** A mutable or immutable collection. */
public interface Container[T]  extends Iterable[T] {
  public def size(): Long;
  public def isEmpty(): Boolean;
  public def contains(y:T): Boolean;
  public def containsAll(c:Container[T]): Boolean;
  public def clone(): Container[T];
}
