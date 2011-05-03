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

package x10.io;

import x10.util.NoSuchElementException;
import x10.util.Box;

/**
 * Usage:
 *
 * try {
 *    val input = new File(inputFileName);
 *    val output = new File(outputFileName);
 *    val p = output.printer();
 *    for (line in input.lines()) {
 *       p.println(line);
 *    }
 *    p.flush();
 * } catch (IOException) { }
 */    
public class ReaderIterator[T] implements Iterator[T], Iterable[T] {
    val r: Reader;
    val m: Marshal[T];
    var next: Box[T];
    
    public def this(m: Marshal[T], r: Reader) {
       this.m = m;
       this.r = r;
       //       this.next = null;
    }
    
    /** Allow the iterator to be used in a for loop. */
    public def iterator(): Iterator[T] = this;

    public def next(): T = {
        if (! hasNext())
            throw new NoSuchElementException();
        val x: T = next.value;
        next = null;
        return x;
    }
    
    public def hasNext(): Boolean {
        if (next == null) {
            try {
                val x: T = r.read[T](m);
                next = new Box[T](x);
            }
            catch (IOException) {
                return false;
            }
        }
        return true;
    }
}

