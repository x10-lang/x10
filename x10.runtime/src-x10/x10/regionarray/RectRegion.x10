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

package x10.regionarray;

import x10.compiler.CompilerFlags;
import x10.compiler.Inline;

/**
 * A RectRegion is a finite dense rectangular region with a specified rank.
 * This class implements a specialization of PolyRegion.
 */
final class RectRegion extends Region{rect} {

    private val size:long;       /* Will be < 0 iff the true size of the region is not expressible as a long */
    private val mins:Rail[long]; /* will be null if rank<5 */
    private val maxs:Rail[long]; /* will be null if rank<5 */

    private val min0:long;
    private val min1:long;
    private val min2:long;
    private val min3:long;
    private val max0:long;
    private val max1:long;
    private val max2:long;
    private val max3:long;

    // cached polyRep representation; space inefficient, so don't want to serialize it.
    transient protected var polyRep:Region(rank)=null;

    private static def allZeros(a:Rail[long]) {
       for (i in a.range) if (a(i) != 0L) return false;
       return true;
    }

    /**
     * Create a rectangular region containing all points p such that min <= p and p <= max.
     */
    def this(minArg:Rail[long], maxArg:Rail[long]) /* TODO: asInt constrant RectRegion{self.rank==minArg.size as Int} */ {
        super(minArg.size as int, true, allZeros(minArg));

	if (minArg.size != maxArg.size) throw new IllegalArgumentException("size of min and max args are not equal");

        var s:long = 1;
        for (i in minArg.range) {
	    var rs:long = maxArg(i) - minArg(i) + 1;
            if (rs < 0) rs = 0;
            if (maxArg(i) == Long.MAX_VALUE && minArg(i) == Long.MIN_VALUE) {
                s = -1L;
                break;
            }
            s *= rs;
        }
        size = s;

        if (minArg.size>0) {
            min0 = minArg(0);
            max0 = maxArg(0);
        } else {
            min0 = max0 = 0L;
        }

        if (minArg.size>1) {
            min1 = minArg(1);
            max1 = maxArg(1);
        } else {
            min1 = max1 = 0L;
        }

        if (minArg.size>2) {
            min2 = minArg(2);
            max2 = maxArg(2);
        } else {
            min2 = max2 = 0L;
        }

        if (minArg.size>3) {
            min3 = minArg(3);
            max3 = maxArg(3);
        } else {
            min3 = max3 = 0L;
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
    @Inline def this(min:long, max:long):RectRegion{self.rank==1,self.rect} {
        super(1, true, min==0L);

        size = (min == Long.MIN_VALUE && max == Long.MAX_VALUE) ? -1L : max - min + 1;
        min0 = min;
        max0 = max;

        min1 = min2 = min3 = 0L;
        max1 = max2 = max3 = 0L;
        mins = null;
        maxs = null;
    }

    public def size():long {
        if (size < 0) throw new UnboundedRegionException("size exceeds capacity of long");
        return size;
    }

    public def isConvex() = true;

    public def isEmpty() = size == 0L;

    public def indexOf(pt:Point):long {
	if (!contains(pt)) return -1L;
        var offset: long = pt(0) - min(0);
        for (i in 1..(rank-1)) {
            val min_i = min(i);
            val max_i = max(i);
            val pt_i = pt(i);
            val delta_i = max_i - min_i + 1;
            offset = offset*delta_i + pt_i - min_i;
        }
        return offset;
    }

    public def indexOf(i0:long):long {
        if (zeroBased) {
	    if (rank != 1 || !containsInternal(i0)) return -1L;
            return i0;
        } else { 
	    if (rank != 1 || !containsInternal(i0)) return -1L;
            return i0 - min0;
        }
    }

    public def indexOf(i0:long, i1:long):long {
        if (zeroBased) {
	    if (rank != 2 || !containsInternal(i0,i1)) return -1L;
            var offset:long = i0;
            offset = offset*(max1 + 1) + i1;
            return offset;
        } else { 
	    if (rank != 2 || !containsInternal(i0,i1)) return -1;
            var offset:long = i0 - min0;
            offset = offset*(max1 - min1 + 1) + i1 - min1;
            return offset;
        }
    }

    public def indexOf(i0:long, i1:long, i2:long):long {
        if (zeroBased) {
	    if (rank != 3 || !containsInternal(i0,i1,i2)) return -1L;
            var offset:long = i0;
            offset = offset*(max1 + 1) + i1;
            offset = offset*(max2 + 1) + i2;
            return offset;
        } else { 
	    if (rank != 3 || !containsInternal(i0,i1,i2)) return -1L;
            var offset:long = i0 - min0;
            offset = offset*(max1 - min1 + 1) + i1 - min1;
            offset = offset*(max2 - min2 + 1) + i2 - min2;
            return offset;
        }
    }

    public def indexOf(i0:long, i1:long, i2:long, i3:long):long {
        if (zeroBased) {
	    if (rank != 4 || !containsInternal(i0,i1,i2,i3)) return -1L;
            var offset:long = i0;
            offset = offset*(max1 + 1) + i1;
            offset = offset*(max2 + 1) + i2;
            offset = offset*(max3 + 1) + i3;
            return offset;
        } else { 
	    if (rank != 4 || !containsInternal(i0,i1,i2,i3)) return -1L;
            var offset:long = i0 - min0;
            offset = offset*(max1 - min1 + 1) + i1 - min1;
            offset = offset*(max2 - min2 + 1) + i2 - min2;
            offset = offset*(max3 - min3 + 1) + i3 - min3;
            return offset;
        }
    }


    public def min(i:int):long {
        if (i<0 || i>=rank) throw new ArrayIndexOutOfBoundsException("min: "+i+" is not a valid rank for "+this);
	switch(i) {
	    case 0: return min0;
	    case 1: return min1;
	    case 2: return min2;
	    case 3: return min3;
	    default: return mins(i);
	}
    }

    public def max(i:int):long {
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
    
    public def min():(int)=>long = (i:int)=> min(i);
    public def max():(int)=>long = (i:int)=> max(i);

    public def contains(that:Region(rank)): boolean {
       if (that instanceof RectRegion) {
           val thatMin = (that as RectRegion).min();
           val thatMax = (that as RectRegion).max();
           for (i in 0..(rank-1)) {
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
               for (r in (p.rank-1)..4) {
                   if (p(r)<mins(r) || p(r)>maxs(r)) return false;
               }
           case 3: { val tmp = p(3); if (tmp<min3 || tmp>max3) return false; }
           case 2: { val tmp = p(2); if (tmp<min2 || tmp>max2) return false; }
           case 1: { val tmp = p(1); if (tmp<min1 || tmp>max1) return false; }
           case 0: { val tmp = p(0); if (tmp<min0 || tmp>max0) return false; }
        }
	return true;
    }

    public def contains(i0:long){rank==1}:boolean = containsInternal(i0);
    public def contains(i0:long, i1:long){rank==2}:boolean = containsInternal(i0,i1);
    public def contains(i0:long, i1:long, i2:long){rank==3}:boolean = containsInternal(i0,i1,i2);
    public def contains(i0:long, i1:long, i2:long, i3:long){rank==4}:boolean = containsInternal(i0,i1,i2,i3);

    private def containsInternal(i0:long):boolean {
        return i0>=min0 && i0<=max0;
    }

    private def containsInternal(i0:long, i1:long):boolean { 
        if (CompilerFlags.useUnsigned() && zeroBased) {
            return ((i0 as ULong) <= (max0 as ULong)) &&
                   ((i1 as ULong) <= (max1 as ULong));
        } else {
            return i0>=min0 && i0<=max0 && 
                   i1>=min1 && i1<=max1;
        }
    }

    private def containsInternal(i0:long, i1:long, i2:long):boolean {
        if (CompilerFlags.useUnsigned() && zeroBased) {
            return ((i0 as ULong) <= (max0 as ULong)) &&
                   ((i1 as ULong) <= (max1 as ULong)) &&
                   ((i2 as ULong) <= (max2 as ULong));
        } else {
            return i0>=min0 && i0<=max0 && 
                   i1>=min1 && i1<=max1 && 
                   i2>=min2 && i2<=max2;
        }
    }

    private def containsInternal(i0:long, i1:long, i2:long, i3:long):boolean {
        if (CompilerFlags.useUnsigned() && zeroBased) {
            return ((i0 as ULong) <= (max0 as ULong)) &&
                   ((i1 as ULong) <= (max1 as ULong)) &&
                   ((i2 as ULong) <= (max2 as ULong)) &&
                   ((i3 as ULong) <= (max3 as ULong));
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
            polyRep = Region.makeRectangularPoly(new Rail[long](rank, (i:long)=>min(i as int)), 
                                                 new Rail[long](rank, (i:long)=>max(i as int))) as Region(rank);
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
	    val newMin = new Rail[long](rank, (i:long)=>Math.max(min(i as int), thatMin(i as int)));
	    val newMax = new Rail[long](rank, (i:long)=>Math.min(max(i as int), thatMax(i as int)));
	    for (i in 0..(newMin.size-1)) {
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
            val newMin = new Rail[long](k, (i:long)=>i<rank?min(i as int):thatMin((i as int)-rank));
            val newMax = new Rail[long](k, (i:long)=>i<rank?max(i as int):thatMax((i as int)-rank));
            return new RectRegion(newMin, newMax);
        } else if (that instanceof RectRegion1D) {
            val thatMin = that.min(0);
            val thatMax = that.max(0);
            val k = rank+1;
            val newMin = new Rail[long](k, (i:long)=>i<rank?min(i as int):thatMin);
            val newMax = new Rail[long](k, (i:long)=>i<rank?max(i as int):thatMax);
            return new RectRegion(newMin, newMax);
        } else if (that instanceof FullRegion) {
       	    val k = rank+that.rank;
            val newMin = new Rail[long](k, (i:long)=>i<rank?min(i as int):Long.MIN_VALUE);
            val newMax = new Rail[long](k, (i:long)=>i<rank?max(i as int):Long.MAX_VALUE);
	    return new RectRegion(newMin,newMax);
        } else {
	   return (toPolyRegion() as Region(rank)).product(that);
        }
    }

    public def translate(v: Point(rank)):Region(rank){self.rect} {
        val newMin = new Rail[long](rank, (i:long)=>min(i as int)+v(i as int));
        val newMax = new Rail[long](rank, (i:long)=>max(i as int)+v(i as int));
        return new RectRegion(newMin, newMax) as Region(rank){self.rect};
    }

    public def projection(axis:int):Region(1){self.rect} {
        return new RectRegion(min(axis), max(axis));
    }

    public def eliminate(axis:int):Region{self.rect} /*(rank-1)*/ {
    	val k = rank-1;
        val newMin = new Rail[long](k, (i:long)=>i<axis?min(i as int):min((i as int)+1));
        val newMax = new Rail[long](k, (i:long)=>i<axis?max(i as int):max((i as int)+1));
        return new RectRegion(newMin, newMax);
    }    


    private static class RRIterator(myRank:int) implements Iterator[Point(myRank)] {
        val min:(int)=>long;
        val max:(int)=>long;
        var done:boolean;
        val cur:Rail[long]/*{(self.size as Int)==myRank}*/;

        def this(rr:RectRegion):RRIterator{self.myRank==rr.rank} {
            property(rr.rank);
	    val t = rr.min();
            min = t;
            max = rr.max();
            done = rr.size == 0L;
            cur = new Rail[long](myRank, (l:long)=>t(l as int));
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
            return ans as Point(myRank);
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
