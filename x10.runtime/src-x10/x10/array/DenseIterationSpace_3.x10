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

package x10.array;

/**
 * A DenseIterationSpace_3 represents the rank 3 
 * iteration space of points [min0,min1,min2]..[max0,max1,max2] inclusive.
 */
public final class DenseIterationSpace_3 extends IterationSpace(3){rect} {

    public val min0:Long;
    public val min1:Long;
    public val min2:Long;
    public val max0:Long;
    public val max1:Long;
    public val max2:Long;

    public static val EMPTY = new DenseIterationSpace_3(0,0,0,-1,-1,-1);

    public def this(min0:Long, min1:Long, min2:Long, max0:Long, max1:Long, max2:Long) {
        super(3,true);
        this.min0 = min0;
        this.min1 = min1;
        this.min2 = min2;
        this.max0 = max0;
        this.max1 = max1;
        this.max2 = max2;
    }

    public def min(i:Long):Long {
        if (i == 0) return min0;
        if (i == 1) return min1;
        if (i == 2) return min2;
        throw new IllegalOperationException(i +" is not a valid rank");
    }

    public def max(i:Long):Long {
        if (i == 0) return max0;
        if (i == 1) return max1;
        if (i == 2) return max2;
        throw new IllegalOperationException(i +" is not a valid rank");
    }

    public def isEmpty() = max0 < min0 || max1 < min1 || max2 < min2;

    public def iterator():Iterator[Point(3)] = new DIS3_It();

    private class DIS3_It implements Iterator[Point(3)] {
        var cur0:Long;
        var cur1:Long;
        var cur2:Long;

        def this() {
            cur0 = min0;
            cur1 = min1;
            cur2 = min2;
        }

        public def hasNext() = cur0 <= max0 && cur1 <= max1 && cur2 <= max2;

        public def next() {
           val p = Point.make(cur0, cur1, cur2);
           cur2 += 1;
           if (cur2 > max2) {
               cur2 = min2;
               cur1 += 1;
	       if (cur1 > max1) {
                   cur1 = min1;
                   cur0 += 1;
               }
           }
           return p;
        }
    }
}
