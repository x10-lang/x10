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

package x10.regionarray;

import x10.compiler.CompilerFlags;

/**
 * A RectRegion1D is a finite dense rectangular region of rank 1.
 * This is a space-optimized implementation of the more general
 * RectRegion class intended primarily to reduce the memory
 * overhead and serialization cost of Array meta-data.
 */
final class RectRegion1D extends Region{rect,rank==1} {
    private val size:Long;            /* Will be < 0 iff the true size of the region is not expressible as a long */
    private val min:Long;
    private val max:Long;

    /**
     * Create a 1-dim region min..max.
     */
    def this(minArg:Long, maxArg:Long):RectRegion1D{self.rank==1, self.rect} {
        super(1, true, minArg==0);

       
        val s = maxArg - minArg +1;
        if (minArg == Long.MIN_VALUE && maxArg == Long.MAX_VALUE) {
            size = -1; // overflow;
         } else {
            size = s > 0 ? s : 0;
        }        
        min = minArg;
        max = maxArg;
    }

    /**
     * Create a 1-dim region 0..max.
     * Separate constructor so the constraint solver can correctly determine
     * statically that the zeroBased and rail properties are true for all regions
     * made via this constructor.
     */
    def this(maxArg:Long):RectRegion1D{self.rank==1, self.rect, self.rail, self.zeroBased} {
        super(1n);

        size = maxArg +1;
        min = 0;
        max = maxArg;
    }

    public def size():Long {
        if (size < 0) throw new UnboundedRegionException("size exceeds capacity of long");
        return size;
    }

    public def isConvex():Boolean = true;

    public def isEmpty():Boolean = size == 0;

    public def indexOf(pt:Point):Long {
	if (!contains(pt)) return -1;
        return pt(0) - min;
    }

    public def indexOf(i0:Long):Long {
        if (zeroBased) {
	    if (!containsInternal(i0)) return -1;
            return i0;
        } else { 
	    if (!containsInternal(i0)) return -1;
            return i0 - min;
        }
    }

    public def indexOf(i0:Long, i1:Long):Long = -1;

    public def indexOf(i0:Long, i1:Long, i2:Long):Long = -1;

    public def indexOf(i0:Long, i1:Long, i2:Long, i3:Long):Long = -1;


    public def min(i:Long):Long {
        if (i != 0) throw new ArrayIndexOutOfBoundsException("min: "+i+" is not a valid rank for "+this);
        return min;
    }

    public def max(i:Long):Long {
        if (i != 0) throw new ArrayIndexOutOfBoundsException("max: "+i+" is not a valid rank for "+this);
        return max;
    }


    //
    // region operations
    //

    protected def computeBoundingBox():Region(rank)=this; 

    def toRectRegion() = new RectRegion(min, max);
    
    public def min():(Long)=>Long = (i:Long)=> min(i);
    public def max():(Long)=>Long = (i:Long)=> max(i);

    public def contains(that:Region(rank)): Boolean {
       return toRectRegion().contains(that);
    }

    public def contains(p:Point):Boolean {
        return toRectRegion().contains(p);
    }

    public def contains(i0:Long){rank==1}:Boolean = containsInternal(i0);

    private def containsInternal(i0:Long):Boolean {
        return i0>=min && i0<=max;
    }

    public def toPolyRegion() {
        return toRectRegion().toPolyRegion();
    }

    public def intersection(that:Region(rank)):Region(rank) {
        return toRectRegion().intersection(that);
    }

    
    public def product(that:Region):Region{self != null} /*self.rank==this.rank+that.rank*/{
        return toRectRegion().product(that);
    }

    public def translate(v: Point(rank)):Region(rank){self.rect} {
        return new RectRegion1D(min+v(0), max+v(0));
    }

    public def projection(axis:Long):Region(1){self.rect} {
        if (axis == 0) return this;
        throw new ArrayIndexOutOfBoundsException("projection: "+axis+" is not a valid rank for "+this);
    }

    public def eliminate(axis:Long):Region{self.rect} /*(rank-1)*/ {
        return toRectRegion().eliminate(axis);
    }    

    private static class RRIterator implements Iterator[Point(1)] {
        val min:Long;
        val max:Long;
        var cur:Long;

        def this(rr:RectRegion1D) {
            min = rr.min;
            max = rr.max;
            cur = min;
        }        

        public def hasNext() = cur <= max;

        public def next():Point(1) {
            return Point.make(cur++);
        }
    }

    public def iterator():Iterator[Point(1)] {
        return new RRIterator(this);
    }

    public def toString():String {
        return "["+min+".."+max+"]";
    }
}
