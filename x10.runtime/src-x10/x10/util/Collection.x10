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

/** A mutable collection. */
public interface Collection[T] extends Container[T] {
  public def add(v:T): Boolean;
  public def remove(v:T): Boolean;
  public def addAll(c:Container[T]): Boolean;
  public def retainAll(c:Container[T]): Boolean;
  public def removeAll(c:Container[T]): Boolean;
  public def addAllWhere(c:Container[T], p:(T) => Boolean): Boolean;
  public def removeAllWhere(p:(T) => Boolean): Boolean;
  public def clear(): void;
  public def clone(): Collection[T];
}
