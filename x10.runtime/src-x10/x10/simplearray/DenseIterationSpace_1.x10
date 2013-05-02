/*
 *  This file is part of the X10 project (http://x10-lang.org).
 * 
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 * 
 *  (C) Copyright IBM Corporation 2006-2013.
 */

package x10.simplearray;

/**
 * A DenseIterationSpace_1 represents the rank 1 
 * iteration space of points [min]..[max] inclusive.
 */
public final class DenseIterationSpace_1 extends IterationSpace(1) {

    public val min:long;
    public val max:long;

    public def this(min:long, max:long) {
        super(1);
        this.min = min;
        this.max = max;
    }

    public def min(i:int) {
       if (i != 0) throw new IllegalOperationException(i +" is not a valid rank");
       return min;
    }

    public def max(i:int) {
       if (i != 0) throw new IllegalOperationException(i +" is not a valid rank");
       return max;
    }

    public def iterator():Iterator[Point(1)] = new DIS_1_It(min, max);

    private static class DIS_1_It implements Iterator[Point(1)] {
        var cur:long;
        val last:long;
        
        def this(min:long, max:long) { 
           this.cur = min;
           this.last = max;
        }
 
        public def hasNext() = cur <= last;
        public def next() = Point.make(cur++);

    }
}
