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

package safe.array;

import x10.compiler.CompilerFlags;
import safe.lang.Int;
import safe.lang.Boolean; 
import safe.array.Point;
import safe.array.Region; 
import safe.array.RectRegion1D;
import safe.array.Array; 


/**
 * A RectRegion1D is a finite dense rectangular region of rank 1.
 * This is a space-optimized implementation of the more general
 * RectRegion class intended primarily to reduce the memory
 * overhead and serialization cost of Array meta-data.
 */
public final class RectRegion1D extends Region{rect == Boolean.TRUE(),rank==Int.asInt(1)} {
    private val size:Int;           /* Will be < 0 iff the true size of the region is not expressible as an Int */
    private val min:Int;
    private val max:Int;

    /**
     * Create a 1-dim region min..max.
     */
    /*
    def this(minArg:Int, maxArg:Int):RectRegion1D{self.rank==Int.asInt(1), self.rect == Boolean.TRUE()} {
        super(1, true, Int.sequals(minArg,0));
        
        // val s = (maxArg as long) - (minArg as long) +1L;
        // if (s > Int.MAX_VALUE as Long) {
        //     size = -1; // encode overflow
        // } else {
        //     size = s as Int;
        // }
        size = maxArg - minArg + 1; 
        min = minArg;
        max = maxArg;
    }
     * */

    def this(minArg:Int, maxArg:Int):RectRegion1D{self.rank==Int.asInt(1), 
    											  self.rect == Boolean.TRUE(), 
    											  /*self.zeroBased == Boolean.asBoolean(minArg == Int.asInt(0))*/
    											  self.zeroBased == Int.sequals(minArg, Int.asInt(0))} {
  
    	super(1, true, minArg.equals_(Int.asInt(0) as Int{self == Int.asInt(0)}));
        
        // val s = (maxArg as long) - (minArg as long) +1L;
        // if (s > Int.MAX_VALUE as Long) {
        //     size = -1; // encode overflow
        // } else {
        //     size = s as Int;
        // }
        size = maxArg - minArg + 1; 
        min = minArg;
        max = maxArg;
    }
    
    
    /**
     * Create a 1-dim region 0..max.
     * Separate constructor so the constraint solver can correctly determine
     * statically that the zeroBased and rail properties are true for all regions
     * made via this constructor.
     */
    def this(maxArg:Int):RectRegion1D{self.rank==Int.asInt(1), self.rect == Boolean.TRUE(), self.rail==Boolean.TRUE(), self.zeroBased==Boolean.TRUE()} {
        this(0, maxArg); 
    	//super(1);

        // val s = (maxArg as long) +1L;
        // if (s > Int.MAX_VALUE as Long) {
        //     size = -1; // encode overflow
        // } else {
        //     size = s as Int;
        // }
        //size = maxArg + 1; 
        //min = 0;
        //max = maxArg;
    }

    public def size():Int {
      if ((size < 0)()) throw new UnboundedRegionException("size exceeds capacity of Int");
      return size;
    }

    public def isConvex() = Boolean.TRUE();

    public def isEmpty() = Int.sequals(size, 0);

    public def indexOf(pt:Point) :Int  {
	if (!contains(pt)()) return -1;
        return pt(0) - min;
    }

    public def indexOf(i0:Int):Int {
        if (zeroBased()()) {
	    if (!containsInternal(i0)()) return -1;
            return i0;
        } else { 
	    if (!containsInternal(i0)()) return -1;
            return i0 - min;
        }
    }

    public def indexOf(i0:Int, i1:Int):Int {
        return -1;
    }

    public def indexOf(i0:Int, i1:Int, i2:Int):Int {
        return -1;
    }

    public def indexOf(i0:Int, i1:Int, i2:Int, i3:Int):Int {
        return -1;
    }


    public def min(i:Int):Int {
        if (!Int.sequals(i, 0)()) throw new ArrayIndexOutOfBoundsException("min: "+i+" is not a valid rank for "+this);
        return min;
    }

    public def max(i:Int):Int {
        if (!Int.sequals(i, 0)()) throw new ArrayIndexOutOfBoundsException("max: "+i+" is not a valid rank for "+this);
        return max;
    }


    //
    // region operations
    //

    protected def computeBoundingBox():Region(rank)=this; 

    def toRectRegion() = new RectRegion(min, max);
    
    public def min():(Int)=>Int = (i:Int)=> min(i);
    public def max():(Int)=>Int = (i:Int)=> max(i);

    public def contains(that:Region(rank)): Boolean {
       return toRectRegion().contains(that);
    }

    public def contains(p:Point):Boolean {
        return toRectRegion().contains(p);
    }

    public def contains(i0:Int){rank==Int.asInt(1)}:Boolean = containsInternal(i0);

    private def containsInternal(i0:Int):Boolean {
        return i0>=min && i0<=max;
    }

    // public def toPolyRegion() {
    //     return toRectRegion().toPolyRegion();
    // }

    public def Intersection(that:Region(rank)):Region(rank) {
        return toRectRegion().Intersection(that);
    }

    
    public def product(that:Region):Region{self != null} /*self.rank==this.rank+that.rank*/{
        return toRectRegion().product(that);
    }

    public def translate(v: Point(rank)):Region(rank){self.rect == Boolean.TRUE()} {
        return new RectRegion1D(min+v(0), max+v(0));
    }

    public def projection(axis:Int):Region(Int.asInt(1)){self.rect == Boolean.TRUE()} {
        if ((Int.sequals(axis,0))()) return this;
        throw new ArrayIndexOutOfBoundsException("projection: "+axis+" is not a valid rank for "+this);
    }

    // public def eliminate(axis:Int):Region{self.rect == Boolean.TRUE()} /*(rank-1)*/ {
    //     return toRectRegion().eliminate(axis);
    // }    

    private static class RRIterator implements Iterator[Point(Int.asInt(1) as Int{self == Int.asInt(1)} )] {
        val min:Int;
        val max:Int;
        var cur:Int;

        def this(rr:RectRegion1D) : RRIterator {
            min = rr.min;
            max = rr.max;
            cur = min;
        }        

        public def hasNext() = (cur <= max)();
        // FIXME: can this be done any better?
        
        public def next():Point(Int.asInt(1) as Int{self == Int.asInt(1)}) {
        	val res = Point.make(cur) as Point(Int.asInt(1) as Int{self == Int.asInt(1)});
        	cur = cur + 1; 
            return res;
        }
    }

    public def iterator():Iterator[Point(Int.asInt(1) as Int{self == Int.asInt(1)})] {
        return new RRIterator(this);
    }

    public def toString():String {
        return "["+min+".."+max+"]";
    }
}
