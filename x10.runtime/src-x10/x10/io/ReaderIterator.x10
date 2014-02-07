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

package x10.io;

import x10.util.NoSuchElementException;

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
public class ReaderIterator[T] { T haszero } implements Iterator[T], Iterable[T] {
    val r:Reader;
    val m:Marshal[T];
    var next:T;
    
    public def this(m:Marshal[T], r:Reader) {
       this.m = m;
       this.r = r;
    }
    
    /** Allow the iterator to be used in a for loop. */
    public def iterator():Iterator[T] = this;

    public def next():T = {
        if (!hasNext())
            throw new NoSuchElementException();
        val x:T = next;
	next = Zero.get[T]();
        return x;
    }
    
    public def hasNext(): Boolean {
        if (next == Zero.get[T]()) {
            try {
                next = r.read[T](m);
            }
            catch (IOException) {
                return false;
            }
        }
        return true;
    }
}

