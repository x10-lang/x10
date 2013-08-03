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

package x10.array;

/**
 * A DenseIterationSpace_2 represents the rank 2 
 * iteration space of points [min0,min1]..[max0,max1] inclusive.
 */
public final class DenseIterationSpace_2 extends IterationSpace(2){rect} {

    public val min0:long;
    public val min1:long;
    public val max0:long;
    public val max1:long;

    public def this(min0:long, min1:long, max0:long, max1:long) {
        super(2, true);
        this.min0 = min0;
        this.min1 = min1;
        this.max0 = max0;
        this.max1 = max1;
    }

    public def min(i:long):long {
       if (i == 0) return min0;
       if (i == 1) return min1;
       throw new IllegalOperationException(i +" is not a valid rank");
    }

    public def max(i:long):long {
        if (i == 0) return max0;
        if (i == 1) return max1;
        throw new IllegalOperationException(i +" is not a valid rank");
    }

    public def iterator():Iterator[Point(2)] = new DIS2_It();

    private class DIS2_It implements Iterator[Point(2)] {
        var cur0:long;
        var cur1:long;

        def this() {
            cur0 = min0;
            cur1 = min1;
        }

        public def hasNext() = cur0 <= max0 && cur1 <= max1;

        public def next() {
           val p = Point.make(cur0, cur1);
           cur1 += 1;
           if (cur1 > max1) {
               cur1 = min1;
               cur0 += 1;
           }
           return p;
        }
    }
}
