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

package x10.util.resilient;

import x10.util.Box;

import java.util.Iterator;

/**
 * This class is based on Java's Iterator interface.  It is used when it is necessary
 * to access a Java-implemented iterator. 
 */

public class IteratorJava {

    var javaIterator: java.util.Iterator;

    public def this(iterator: java.util.Iterator) {
      javaIterator = iterator;
    }

    /**
     * Returns true if the iteration has more elements.
     */
    public def hasNext(): Boolean {
        return javaIterator.hasNext();
    };

    /**
     * Return the next element in the iteration.
     */
    public def next(): Any {
        return javaIterator.next() as Any;
    };

    /**
     * Remove from the underlying collection the last element returned by this iterator.
     */
    public def remove(): void {
        javaIterator.remove();
    };

}
