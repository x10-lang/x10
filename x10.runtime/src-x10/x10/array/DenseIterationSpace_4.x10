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
 * A DenseIterationSpace_4 represents the rank 4 
 * iteration space of points [min0,min1,min2]..[max0,max1,max2] inclusive.
 */
public final class DenseIterationSpace_4 extends IterationSpace(4){rect} {

    public val min0:Long;
    public val min1:Long;
    public val min2:Long;
    public val min3:Long;
    public val max0:Long;
    public val max1:Long;
    public val max2:Long;
    public val max3:Long;

    public static val EMPTY = new DenseIterationSpace_4(0,0,0,0,-1,-1,-1,-1);

    public def this(min0:Long, min1:Long, min2:Long, min3:Long, 
                    max0:Long, max1:Long, max2:Long, max3:Long) {
        super(4,true);
        this.min0 = min0;
        this.min1 = min1;
        this.min2 = min2;
        this.min3 = min3;
        this.max0 = max0;
        this.max1 = max1;
        this.max2 = max2;
        this.max3 = max3;
    }

    public def min(i:Long):Long {
        if (i == 0) return min0;
        if (i == 1) return min1;
        if (i == 2) return min2;
        if (i == 3) return min3;
        throw new IllegalOperationException(i +" is not a valid rank");
    }

    public def max(i:Long):Long {
        if (i == 0) return max0;
        if (i == 1) return max1;
        if (i == 2) return max2;
        if (i == 3) return max3;
        throw new IllegalOperationException(i +" is not a valid rank");
    }

    public def isEmpty() = max0 < min0 || max1 < min1 || max2 < min2 || max3 < min3;

    public def size() = (max0 - min0 + 1) * (max1 - min1 + 1) * (max2 - min2 + 1) * (max3 - min3 + 1);

    public def iterator():Iterator[Point(4)] = new DIS4_It();

    private class DIS4_It implements Iterator[Point(4)] {
        var cur0:Long;
        var cur1:Long;
        var cur2:Long;
        var cur3:Long;

        def this() {
            cur0 = min0;
            cur1 = min1;
            cur2 = min2;
            cur3 = min3;
        }

        public def hasNext() = cur0 <= max0 && cur1 <= max1 && cur2 <= max2 && cur3 <= max3;

        public def next() {
           val p = Point.make(cur0, cur1, cur2, cur3);
           cur3 +=1;
           if (cur3 > max3) {
               cur3 = min3;
               cur2 += 1;
               if (cur2 > max2) {
                   cur2 = min2;
                   cur1 += 1;
	           if (cur1 > max1) {
                       cur1 = min1;
                       cur0 += 1;
                   }
               }
           }
           return p;
        }
    }

    public def toString():String {
        return "["
            +min0+".."+max0+","
            +min1+".."+max1+","
            +min2+".."+max2+","
            +min3+".."+max3
            +"]";
    }
}
