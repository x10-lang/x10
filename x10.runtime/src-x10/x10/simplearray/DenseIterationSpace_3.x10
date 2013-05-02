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
 * A DenseIterationSpace_3 represents the rank 3 
 * iteration space of points [min1,min2,min3]..[max1,max2.max3] inclusive.
 */
public final class DenseIterationSpace_3 extends IterationSpace(3) {

    public val min0:long;
    public val min1:long;
    public val min2:long;
    public val max0:long;
    public val max1:long;
    public val max2:long;

    public def this(min0:long, min1:long, min2:long, max0:long, max1:long, max2:long) {
        super(3);
        this.min0 = min0;
        this.min1 = min1;
        this.min2 = min2;
        this.max0 = max0;
        this.max1 = max1;
        this.max2 = max2;
    }

    public def min(i:int) {
       switch (i) {
           case 0: return min0;
           case 1: return min1;
           case 2: return min2;
           default: throw new IllegalOperationException(i +" is not a valid rank");
       }
    }

    public def max(i:int) {
       switch (i) {
           case 0: return max0;
           case 1: return max1;
           case 2: return max2;
           default: throw new IllegalOperationException(i +" is not a valid rank");
       }
    }

    public def iterator():Iterator[Point(3)] = new DIS3_It();

    private class DIS3_It implements Iterator[Point(3)] {
        var cur0:long;
        var cur1:long;
        var cur2:long;

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
