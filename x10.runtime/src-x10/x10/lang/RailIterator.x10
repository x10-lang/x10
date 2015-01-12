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

/**
 * A package-level implementation of the Iterator[T] interface for Rail[T].
 * Factored out into a separate class to simplify the handwritten native 
 * code that backs the @NativeRep implementation of Rail.
 */
package x10.lang;

final class RailIterator[T] implements Iterator[T] {
  val rail:Rail[T];
  var cur:Long;

  def this(x:Rail[T]) { 
    this.rail = x;
    cur = 0;
  }

  public def next():T = rail(cur++);
  
  public def hasNext():Boolean = cur < rail.size;

}
