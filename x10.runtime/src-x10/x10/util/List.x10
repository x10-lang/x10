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

public interface List[T] extends Collection[T], Indexed[T], Settable[Long,T] {
  public def indices(): List[Long];
  
  public def addBefore(i: Long, v:T): void;
  public def removeAt(i: Long): T;
  public def indexOf(v:T): Long;
  public def lastIndexOf(v:T): Long;
  public def indexOf(index:Long, v:T): Long;
  public def lastIndexOf(index:Long, v:T): Long;
  public def iterator(): ListIterator[T];
  public def iteratorFrom(i: Long): ListIterator[T];
  public def subList(fromIndex: Long, toIndex: Long): List[T];
  
  public def removeFirst(): T;
  public def removeLast(): T;
  public def getFirst(): T;
  public def getLast(): T;

  public def reverse(): void;
  
  public def sort() {T <: Comparable[T]} : void;
  public def sort(cmp: (T,T)=>Int): void;
  // public def sort(lessThan: (T,T)=>Boolean): void;
}
