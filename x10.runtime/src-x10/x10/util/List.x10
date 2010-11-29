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

public interface List[T] extends Collection[T], Indexed[T], Settable[Int,T] {
  public def indices(): List[Int];
  
  public def addBefore(i: Int, T): void;
  public def removeAt(i: Int): T;
  public def indexOf(T): Int;
  public def lastIndexOf(T): Int;
  public def indexOf(Int, T): Int;
  public def lastIndexOf(Int, T): Int;
  public def iterator(): ListIterator[T];
  public def iteratorFrom(i: Int): ListIterator[T];
  public def subList(fromIndex: Int, toIndex: Int): List[T];
  
  public def removeFirst(): T;
  public def removeLast(): T;
  public def getFirst(): T;
  public def getLast(): T;

  public def reverse(): void;
  
  public def sort() {T <: Comparable[T]} : void;
  public def sort(cmp: (T,T)=>Int): void;
  // public def sort(lessThan: (T,T)=>Boolean): void;
}
