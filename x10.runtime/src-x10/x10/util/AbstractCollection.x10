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
public abstract class AbstractCollection[T] extends AbstractContainer[T] implements Collection[T] {
  public abstract def add(v:T): Boolean;
  public abstract def remove(v:T): Boolean;
  
  public def addAll(c: Container[T]): Boolean = addAllWhere(c, (T) => true);
  public def retainAll(c: Container[T]): Boolean = removeAllWhere((x:T) => !c.contains(x));
  public def removeAll(c: Container[T]): Boolean = removeAllWhere((x:T) => c.contains(x));

  public def addAllWhere(c: Container[T], p: (T) => Boolean): Boolean {
      var result: Boolean = false;
      for (x: T in c) {
          if (p(x))
              result |= add(x);
      }
      return result;
  }
  
  public def removeAllWhere(p: (T) => Boolean): Boolean {
     var result: Boolean = false;
     for (x: T in this.clone()) {
         if (p(x))
             result |= remove(x);
     }
     return result;
  }
  
  public def clear(): void { removeAllWhere((T)=>true); }
  
  public abstract def clone(): Collection[T];
}
