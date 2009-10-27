/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.io;

import x10.util.NoSuchElementException;

/**
 * Usage:
 *
 * try {
 *   val in = new File(inputFileName);
 *   val out = new File(outputFileName);
 *   val p = out.printer();
 *   for (line in in.lines()) {
 *      line = line.chop();
 *      p.println(line);
 *   }
 * }
 * catch (IOException e) { }
 */    
public class ReaderIterator[T] implements Iterator[T], Iterable[T] {
    val r: Reader;
    val m: Marshal[T];
    var next: Box[T];
    
    public def this(m: Marshal[T], r: Reader) {
       this.m = m;
       this.r = r;
       this.next = null;
    }
    
    /** Allow the iterator to be used in a for loop. */
    public def iterator(): Iterator[T] = this;

    public def next(): T = {
        if (! hasNext())
            throw new NoSuchElementException();
        val x: T = next as T;
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

