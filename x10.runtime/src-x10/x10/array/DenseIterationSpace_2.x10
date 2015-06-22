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

package x10.array;

/**
 * A DenseIterationSpace_2 represents the rank 2 
 * iteration space of points [min0,min1]..[max0,max1] inclusive.
 */
public final class DenseIterationSpace_2 extends IterationSpace(2){rect} {

    public val min0:Long;
    public val min1:Long;
    public val max0:Long;
    public val max1:Long;

    public static val EMPTY = new DenseIterationSpace_2(0,0,-1,-1);

    public def this(min0:Long, min1:Long, max0:Long, max1:Long) {
        super(2, true);
        this.min0 = min0;
        this.min1 = min1;
        this.max0 = max0;
        this.max1 = max1;
    }

    public operator this * (that:LongRange):DenseIterationSpace_3{self!=null} {
        return new DenseIterationSpace_3(min0, min1, that.min, max0, max1, that.max);
    }

    public def min(i:Long):Long {
       if (i == 0) return min0;
       if (i == 1) return min1;
       throw new IllegalOperationException(i +" is not a valid rank");
    }

    public def max(i:Long):Long {
        if (i == 0) return max0;
        if (i == 1) return max1;
        throw new IllegalOperationException(i +" is not a valid rank");
    }

    public def isEmpty() = max0 < min0 || max1 < min1;

    public def size() = (max0 - min0 + 1) * (max1 - min1 + 1);

    public def iterator():Iterator[Point(2)] = new DIS2_It();

    private class DIS2_It implements Iterator[Point(2)] {
        var cur0:Long;
        var cur1:Long;

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

    public def toString():String {
        return "["
            +min0+".."+max0+","
            +min1+".."+max1
            +"]";
    }
}
