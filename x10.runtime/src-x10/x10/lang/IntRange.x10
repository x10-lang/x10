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

package x10.lang;

/**
 * A representation of the range of integers [min..max].
 */
public struct IntRange(
               /**
                * The minimum value included in the range
                */
               min:Int,
               
               /**
                * The maximum value included in the range
                */
               max:Int,
               
               /**
                * Is the range zero-based?
                */
               zeroBased: boolean
) implements Iterable[Int] {

    /**
     * Construct a IntRange from min..max
     * @param min the minimum value of the range
     * @param max the maximum value of the range
     */
    public def this(min:Int, max:Int) {
    	val zero:Boolean = min == 0;
        property(min, max, zero);
    }
    
    /**
     * The product of two int ranges is interpreted as if the IntRanges
     * were first converted to Region(1) and then the * operator applied.
     */
// FIX RAIL
//    public operator this * (that:IntRange):Region(2){rect} {
//        return Region.makeRectangular([min, that.min], [max, that.max]);
//    }

    /**
     * Return a new IntRange of the same size of this, but
     * with min/max shifted by i.
     */    
    public def translate(i:int) = new IntRange(min+i, max+i);

    /**
     * Return a new IntRange of the same size of this, but
     * with min/max shifted by p(0).
     */    
//    public def translate(p:Point(1)) = new IntRange(min+p(0), max+p(0));
    
    // FIX RAIL
    //    public operator this && (that:Region(1)): Region(1) = (this as Region(1)) && that;

    // FIX RAIL
    //    public operator this -> (p:Place) = Dist.makeConstant(this as Region(1), p);

    public def toString():String = min+".."+max;
    
    public def equals(that:Any):Boolean {
        if (that instanceof IntRange) {
            val other = that as IntRange;
            return min == other.min && max == other.max;
        }
        return false;
    }
    
    public def hashCode():int = (max-min).hashCode();
    
    public def iterator():Iterator[Int] {
        return new IntRangeIt(min, max);
    }  

    private static class IntRangeIt implements Iterator[Int] {
        var cur:int;
        val max:int;
        def this(min:int, max:int) {
            this.cur = min;
            this.max = max;
        }
        public def hasNext() { return cur <= max; }
        public def next() { return cur++; }
    }
}
