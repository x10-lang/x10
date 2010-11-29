/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.util;

/** A mutable collection. */
public interface Collection[T] extends Container[T] {
  public def add(T): Boolean;
  public def remove(T): Boolean;
  public def addAll(Container[T]): Boolean;
  public def retainAll(Container[T]): Boolean;
  public def removeAll(Container[T]): Boolean;
  public def addAllWhere(Container[T], (T) => Boolean): Boolean;
  public def removeAllWhere((T) => Boolean): Boolean;
  public def clear(): void;
  public def clone(): Collection[T];
}
