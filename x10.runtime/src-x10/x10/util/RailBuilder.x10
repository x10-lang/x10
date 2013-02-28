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

public class RailBuilder[T] implements Builder[T,Rail[T]] {
    val buf: GrowableIndexedMemoryChunk[T];

    public def this() {
        buf = new GrowableIndexedMemoryChunk[T]();
    }

    public def this(size: Int) {
        buf = new GrowableIndexedMemoryChunk[T](size);
    }

    public def add(x: T) {
        buf.add(x);
        return this;
    }

    public def insert(loc:Int, items:Rail[T]) {
        buf.insert(loc, items.raw());
        return this;
    }

    public def length():Int {
        return buf.length();
    }

    public def result():Rail[T] {
        return buf.toRail();
    }
}

