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

public class RailBuilder[T] implements Builder[T,Rail[T]] {
    val buf:GrowableRail[T];

    public def this() {
        buf = new GrowableRail[T]();
    }

    public def this(size:Long) {
        buf = new GrowableRail[T](size);
    }

    public def add(x: T) {
        buf.add(x);
        return this;
    }

    public def insert(loc:Long, items:Rail[T]) {
        buf.insert(loc, items);
        return this;
    }

    public def length():Long {
        return buf.size();
    }

    public def result():Rail[T] {
        return buf.toRail();
    }
}

