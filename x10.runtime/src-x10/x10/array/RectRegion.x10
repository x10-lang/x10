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

package x10.array;

import x10.compiler.CompilerFlags;
import x10.compiler.Inline;

/**
 * A RectRegion is a finite dense rectangular region with a specified rank.
 * This class implements a specialization of PolyRegion.
 */
final class RectRegion extends Region{rect} {

    private val size:int;           /* Will be < 0 iff the true size of the region is not expressible as an int */
    private val mins:Array[int](1); /* will be null if rank<5 */
    private val maxs:Array[int](1); /* will be null if rank<5 */

    private val min0:int;
    private val min1:int;
    private val min2:int;
    private val min3:int;
    private val max0:int;
    private val max1:int;
    private val max2:int;
    private val max3:int;

    // cached polyRep representation; space inefficient, so don't want to serialize it.
    transient protected var polyRep:Region(rank)=null;

    private static def allZeros(a:Array[int](1)) {
       for ([i] in a) if (a(i) != 0) return false;
       return true;
    }

    /**
     * Create a rectangular region containing all points p such that min <= p and p <= max.
     */
    def this(minArg:Array[int](1), maxArg:Array[int](1)):RectRegion{self.rank==minArg.size} {
        super(minArg.size, true, allZeros(minArg));

	if (minArg.size != maxArg.size) throw new IllegalArgumentException("size of min and max args are not equal");

        var s:long = 1;
        for (var i:int = 0; i<minArg.size; i++) {
	    var rs:long = (maxArg(i) as Long) - (minArg(i)) as Long + 1;
            if (rs < 0) rs = 0;
            s *= rs;
        }
        if (s > Int.MAX_VALUE as Long) {
            size = -1; // encode overflow
        } else {
            size = s as Int;
        }

        if (minArg.size>0) {
            min0 = minArg(0);
            max0 = maxArg(0);
        } else {
            min0 = max0 = 0;
        }

        if (minArg.size>1) {
            min1 = minArg(1);
            max1 = maxArg(1);
        } else {
            min1 = max1 = 0;
        }

        if (minArg.size>2) {
            min2 = minArg(2);
            max2 = maxArg(2);
        } else {
            min2 = max2 = 0;
        }

        if (minArg.size>3) {
            min3 = minArg(3);
            max3 = maxArg(3);
        } else {
            min3 = max3 = 0;
        }
	
	if (minArg.size>4) {
	  mins = minArg;
	  maxs = maxArg;
        } else {
	  mins = null;
          maxs = null;
        }
    }

    /**
     * Create a 1-dim region min..max.
     */
    @Inline def this(min:int, max:int):RectRegion{self.rank==1,self.rect} {
        super(1, true, min==0);

        size = max - min + 1;
        min0 = min;
        max0 = max;

        min1 = min2 = min3 = 0;
        max1 = max2 = max3 = 0;
        mins = null;
        maxs = null;
    }

    public def size():int {
      if (size < 0) throw new UnboundedRegionException("size exceeds capacity of int");
      return size;
    }

    public def isConvex() = true;

    public def isEmpty() = size == 0;

    public def indexOf(pt:Point) {
	if (!contains(pt)) return -1;
        var offset: int = pt(0) - min(0);
        for (var i:int=1; i<rank; i++) {
            val min_i = min(i);
            val max_i = max(i);
            val pt_i = pt(i);
            val delta_i = max_i - min_i + 1;
            offset = offset*delta_i + pt_i - min_i;
        }
        return offset;
    }

    public def indexOf(i0:int):int {
        if (zeroBased) {
	    if (rank != 1 || !containsInternal(i0)) return -1;
            return i0;
        } else { 
	    if (rank != 1 || !containsInternal(i0)) return -1;
            return i0 - min0;
        }
    }

    public def indexOf(i0:int, i1:int):int {
        if (zeroBased) {
	    if (rank != 2 || !containsInternal(i0,i1)) return -1;
            var offset:int = i0;
            offset = offset*(max1 + 1) + i1;
            return offset;
        } else { 
	    if (rank != 2 || !containsInternal(i0,i1)) return -1;
            var offset:int = i0 - min0;
            offset = offset*(max1 - min1 + 1) + i1 - min1;
            return offset;
        }
    }

    public def indexOf(i0:int, i1:int, i2:int):int {
        if (zeroBased) {
	    if (rank != 3 || !containsInternal(i0,i1,i2)) return -1;
            var offset:int = i0;
            offset = offset*(max1 + 1) + i1;
            offset = offset*(max2 + 1) + i2;
            return offset;
        } else { 
	    if (rank != 3 || !containsInternal(i0,i1,i2)) return -1;
            var offset:int = i0 - min0;
            offset = offset*(max1 - min1 + 1) + i1 - min1;
            offset = offset*(max2 - min2 + 1) + i2 - min2;
            return offset;
        }
    }

    public def indexOf(i0:int, i1:int, i2:int, i3:int):int {
        if (zeroBased) {
	    if (rank != 4 || !containsInternal(i0,i1,i2,i3)) return -1;
            var offset:int = i0;
            offset = offset*(max1 + 1) + i1;
            offset = offset*(max2 + 1) + i2;
            offset = offset*(max3 + 1) + i3;
            return offset;
        } else { 
	    if (rank != 4 || !containsInternal(i0,i1,i2,i3)) return -1;
            var offset:int = i0 - min0;
            offset = offset*(max1 - min1 + 1) + i1 - min1;
            offset = offset*(max2 - min2 + 1) + i2 - min2;
            offset = offset*(max3 - min3 + 1) + i3 - min3;
            return offset;
        }
    }


    public def min(i:int):int {
        if (i<0 || i>=rank) throw new ArrayIndexOutOfBoundsException("min: "+i+" is not a valid rank for "+this);
	switch(i) {
	    case 0: return min0;
	    case 1: return min1;
	    case 2: return min2;
	    case 3: return min3;
	    default: return mins(i);
	}
    }

    public def max(i:int):int {
        if (i<0 || i>=rank) throw new ArrayIndexOutOfBoundsException("max: "+i+" is not a valid rank for "+this);
	switch(i) {
	    case 0: return max0;
	    case 1: return max1;
	    case 2: return max2;
	    case 3: return max3;
	    default: return maxs(i);
	}
    }


    //
    // region operations
    //

    protected def computeBoundingBox():Region(rank)=this; 
    
    public def min():(int)=>int = (i:int)=> min(i);
    public def max():(int)=>int = (i:int)=> max(i);

    public def contains(that:Region(rank)): boolean {
       if (that instanceof RectRegion) {
           val thatMin = (that as RectRegion).min();
           val thatMax = (that as RectRegion).max();
           for (var i:int =0; i<rank; i++) {
               if (min(i) > thatMin(i)) return false;
               if (max(i) < thatMax(i)) return false;
           }
           return true;
       } else if (that instanceof RectRegion1D) {
           return min(0) <= that.min(0) && max(0) >= that.max(0);
       } else {
           return this.contains(that.computeBoundingBox());
       }
    }

    public def contains(p:Point):boolean {
        if (p.rank != rank) return false;
	// NOTE: intentional fall through of cases!
        switch(p.rank-1) {
           default:
               for ([r] in (p.rank-1)..4) {
                   if (p(r)<mins(r) || p(r)>maxs(r)) return false;
               }
           case 3: { val tmp = p(3); if (tmp<min3 || tmp>max3) return false; }
           case 2: { val tmp = p(2); if (tmp<min2 || tmp>max2) return false; }
           case 1: { val tmp = p(1); if (tmp<min1 || tmp>max1) return false; }
           case 0: { val tmp = p(0); if (tmp<min0 || tmp>max0) return false; }
        }
	return true;
    }

    public def contains(i0:int){rank==1}:boolean = containsInternal(i0);
    public def contains(i0:int, i1:int){rank==2}:boolean = containsInternal(i0,i1);
    public def contains(i0:int, i1:int, i2:int){rank==3}:boolean = containsInternal(i0,i1,i2);
    public def contains(i0:int, i1:int, i2:int, i3:int){rank==4}:boolean = containsInternal(i0,i1,i2,i3);

    private def containsInternal(i0:int):boolean {
        return i0>=min0 && i0<=max0;
    }

    private def containsInternal(i0:int, i1:int):boolean { 
        if (CompilerFlags.useUnsigned() && zeroBased) {
            return ((i0 as UInt) <= (max0 as UInt)) &&
                   ((i1 as UInt) <= (max1 as UInt));
        } else {
            return i0>=min0 && i0<=max0 && 
                   i1>=min1 && i1<=max1;
        }
    }

    private def containsInternal(i0:int, i1:int, i2:int):boolean {
        if (CompilerFlags.useUnsigned() && zeroBased) {
            return ((i0 as UInt) <= (max0 as UInt)) &&
                   ((i1 as UInt) <= (max1 as UInt)) &&
                   ((i2 as UInt) <= (max2 as UInt));
        } else {
            return i0>=min0 && i0<=max0 && 
                   i1>=min1 && i1<=max1 && 
                   i2>=min2 && i2<=max2;
        }
    }

    private def containsInternal(i0:int, i1:int, i2:int, i3:int):boolean {
        if (CompilerFlags.useUnsigned() && zeroBased) {
            return ((i0 as UInt) <= (max0 as UInt)) &&
                   ((i1 as UInt) <= (max1 as UInt)) &&
                   ((i2 as UInt) <= (max2 as UInt)) &&
                   ((i3 as UInt) <= (max3 as UInt));
        } else {
            return i0>=min0 && i0<=max0 && 
                   i1>=min1 && i1<=max1 && 
                   i2>=min2 && i2<=max2 && 
                   i3>=min3 && i3<=max3;
        }
    }


    /**
     * Return a PolyRegion with the same set of points as this region. This permits
     * general algorithms for intersection, restriction etc to be applied to RectRegion's.
     */
    public def toPolyRegion() {
    	if (polyRep==null) {
            polyRep = Region.makeRectangularPoly(new Array[int](rank, min()), new Array[int](rank, max()));
    	}
    	return polyRep;
    }
    
    
    public def intersection(that:Region(rank)):Region(rank) {
        if (that.isEmpty()) {
	       return that;
        } else if (that instanceof FullRegion) {
            return this;
        } else if (that instanceof RectRegion) {
            val thatMin = (that as RectRegion).min();
            val thatMax = (that as RectRegion).max();
	    val newMin = new Array[int](rank, (i:int)=>Math.max(min(i), thatMin(i)));
	    val newMax = new Array[int](rank, (i:int)=>Math.min(max(i), thatMax(i)));
	    for ([i] in 0..(newMin.size-1)) {
                if (newMax(i)<newMin(i)) return Region.makeEmpty(rank);
            }
            return new RectRegion(newMin, newMax) as Region(rank);
        } else if (that instanceof RectRegion1D) {
            val newMin = Math.max(min(0), that.min(0));
            val newMax = Math.min(max(0), that.max(0));
            if (newMax < newMin) return Region.makeEmpty(1) as Region(rank);
            return new RectRegion1D(newMin, newMax) as Region(rank);
        } else {
            // Use the general representation.
            return (toPolyRegion() as Region(rank)).intersection(that);
        }
    }
    

    
    public def product(that:Region):Region{self != null} /*self.rank==this.rank+that.rank*/{
        if (that.isEmpty()) {
            return Region.makeEmpty(rank + that.rank);
        } else if (that instanceof RectRegion) {
            val thatMin = (that as RectRegion).min();
            val thatMax = (that as RectRegion).max();
            val k = rank+that.rank;
            val newMin = new Array[int](k, (i:int)=>i<rank?min(i):thatMin(i-rank));
            val newMax = new Array[int](k, (i:int)=>i<rank?max(i):thatMax(i-rank));
            return new RectRegion(newMin, newMax);
        } else if (that instanceof RectRegion1D) {
            val thatMin = that.min(0);
            val thatMax = that.max(0);
            val k = rank+1;
            val newMin = new Array[int](k, (i:int)=>i<rank?min(i):thatMin);
            val newMax = new Array[int](k, (i:int)=>i<rank?max(i):thatMax);
            return new RectRegion(newMin, newMax);
        } else if (that instanceof FullRegion) {
       	    val k = rank+that.rank;
            val newMin = new Array[int](k, (i:int)=>i<rank?min(i):Int.MIN_VALUE);
            val newMax = new Array[int](k, (i:int)=>i<rank?max(i):Int.MAX_VALUE);
	    return new RectRegion(newMin,newMax);
        } else {
	   return (toPolyRegion() as Region(rank)).product(that);
        }
    }

    public def translate(v: Point(rank)):Region(rank){self.rect} {
        val newMin = new Array[int](rank, (i:int)=>min(i)+v(i));
        val newMax = new Array[int](rank, (i:int)=>max(i)+v(i));
        return new RectRegion(newMin, newMax) as Region(rank){self.rect};
    }

    public def projection(axis:int):Region(1){self.rect} {
        return new RectRegion(min(axis), max(axis));
    }

    public def eliminate(axis:int):Region{self.rect} /*(rank-1)*/ {
    	val k = rank-1;
        val newMin = new Array[int](k, (i:int)=>i<axis?min(i):min(i+1));
        val newMax = new Array[int](k, (i:int)=>i<axis?max(i):max(i+1));
        return new RectRegion(newMin, newMax);
    }    


    private static class RRIterator(myRank:int) implements Iterator[Point(myRank)] {
        val min:(int)=>int;
        val max:(int)=>int;
        var done:boolean;
        val cur:Array[int]{self.rank==1,self.zeroBased,self.rect,self.rail,self.size==myRank};

        def this(rr:RectRegion):RRIterator{self.myRank==rr.rank} {
            property(rr.rank);
            min = rr.min();
            max = rr.max();
            done = rr.size == 0;
            cur = new Array[int](myRank, min);
        }        

        public def hasNext() = !done;

        public def next():Point(myRank) {
            val ans = Point.make(cur);
            if (cur(myRank-1)<max(myRank-1)) {
                cur(myRank-1)++;
            } else {
	        if (myRank == 1) {
	            done = true;
                } else {
	            // reset lowest rank to min and ripple carry
                    cur(myRank-1) = min(myRank-1);
	            cur(myRank-2)++;
	            var carryRank:int = myRank-2;
	            while (carryRank>0 && cur(carryRank) > max(carryRank)) {
                        cur(carryRank) = min(carryRank);
	                cur(carryRank-1)++;
                        carryRank--;
                    }
	            if (carryRank == 0 && cur(0) > max(0)) {
	                done = true;
                    }
                }
            }
            return ans;
        }
    }
    public def iterator():Iterator[Point(rank)] {
        return new RRIterator(this);
    }


    public def equals(thatObj:Any):boolean {
	if (this == thatObj) return true;
        if (!(thatObj instanceof Region)) return false; 
        val that:Region = thatObj as Region;

        // we only handle rect==rect
        if (!(that instanceof RectRegion))
            return super.equals(that);

        // ranks must match
        if (this.rank!=that.rank)
            return false;

        // fetch bounds
        val thisMin = this.min();
        val thisMax = this.max();
        val thatMin = (that as RectRegion).min();
        val thatMax = (that as RectRegion).max();

        // compare 'em
        for (var i: int = 0; i<rank; i++) {
            if (thisMin(i)!=thatMin(i) || thisMax(i)!=thatMax(i))
                return false;
        }
        return true;
    }

    public def toString():String {
        val thisMin = this.min();
        val thisMax = this.max();
        var s: String = "[";
        for (var i: int = 0; i<rank; i++) {
            if (i>0) s += ",";
            s += "" + thisMin(i) + ".." + thisMax(i);
        }
        s += "]";
        return s;
    }
}
