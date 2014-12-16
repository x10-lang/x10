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
    
public class MapIterator[S,T] implements Iterator[T] {
    val i: Iterator[S];
    val f: (S) => T;

    def this(i: Iterator[S], f: (S) => T) {
        this.i = i;
        this.f = f;
    }
        
    public def hasNext(): Boolean = i.hasNext();
    public def next(): T = f(i.next());
}
