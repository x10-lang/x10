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

package x10.regionarray;

import x10.compiler.CompilerFlags;
import x10.compiler.Inline;

/**
 * A RectRegion is a finite dense rectangular region with a specified rank.
 * This class implements a specialization of PolyRegion.
 */
final class RectRegion extends Region{rect} {

    private val size:Long;       /* Will be < 0 iff the true size of the region is not expressible as a long */
    private val mins:Rail[Long]; /* will be null if rank<5 */
    private val maxs:Rail[Long]; /* will be null if rank<5 */

    private val min0:Long;
    private val min1:Long;
    private val min2:Long;
    private val min3:Long;
    private val max0:Long;
    private val max1:Long;
    private val max2:Long;
    private val max3:Long;

    // cached polyRep representation; space inefficient, so don't want to serialize it.
    transient protected var polyRep:Region(rank)=null;

    private static def allZeros(a:Rail[Long]) {
       for (i in a.range) if (a(i) != 0) return false;
       return true;
    }

    /**
     * Create a rectangular region containing all points p such that min <= p and p <= max.
     */
    def this(minArg:Rail[Long], maxArg:Rail[Long]):RectRegion{self.rank==minArg.size} {
        super(minArg.size, true, allZeros(minArg));

	if (minArg.size != maxArg.size) throw new IllegalArgumentException("size of min and max args are not equal");

        var s:Long = 1;
        for (i in minArg.range) {
	    var rs:Long = maxArg(i) - minArg(i) + 1;
            if (rs < 0) rs = 0;
            if (maxArg(i) == Long.MAX_VALUE && minArg(i) == Long.MIN_VALUE) {
                s = -1;
                break;
            }
            s *= rs;
        }
        size = s;

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
    @Inline def this(min:Long, max:Long):RectRegion{self.rank==1,self.rect} {
        super(1, true, min==0);

        size = (min == Long.MIN_VALUE && max == Long.MAX_VALUE) ? -1 : max - min + 1;
        min0 = min;
        max0 = max;

        min1 = min2 = min3 = 0;
        max1 = max2 = max3 = 0;
        mins = null;
        maxs = null;
    }

    public def size():Long {
        if (size < 0) throw new UnboundedRegionException("size exceeds capacity of long");
        return size;
    }

    public def isConvex() = true;

    public def isEmpty() = size == 0;

    public def indexOf(pt:Point):Long {
	if (!contains(pt)) return -1;
        var offset:Long = pt(0n) - min(0n);
        for (i in 1..(rank-1)) {
            val min_i = min(i);
            val max_i = max(i);
            val pt_i = pt(i);
            val delta_i = max_i - min_i + 1;
            offset = offset*delta_i + pt_i - min_i;
        }
        return offset;
    }

    public def indexOf(i0:Long):Long {
        if (zeroBased) {
	    if (rank != 1 || !containsInternal(i0)) return -1;
            return i0;
        } else { 
	    if (rank != 1 || !containsInternal(i0)) return -1;
            return i0 - min0;
        }
    }

    public def indexOf(i0:Long, i1:Long):Long {
        if (zeroBased) {
	    if (rank != 2 || !containsInternal(i0,i1)) return -1;
            var offset:Long = i0;
            offset = offset*(max1 + 1) + i1;
            return offset;
        } else { 
	    if (rank != 2 || !containsInternal(i0,i1)) return -1;
            var offset:Long = i0 - min0;
            offset = offset*(max1 - min1 + 1) + i1 - min1;
            return offset;
        }
    }

    public def indexOf(i0:Long, i1:Long, i2:Long):Long {
        if (zeroBased) {
	    if (rank != 3 || !containsInternal(i0,i1,i2)) return -1;
            var offset:Long = i0;
            offset = offset*(max1 + 1) + i1;
            offset = offset*(max2 + 1) + i2;
            return offset;
        } else { 
	    if (rank != 3 || !containsInternal(i0,i1,i2)) return -1;
            var offset:Long = i0 - min0;
            offset = offset*(max1 - min1 + 1) + i1 - min1;
            offset = offset*(max2 - min2 + 1) + i2 - min2;
            return offset;
        }
    }

    public def indexOf(i0:Long, i1:Long, i2:Long, i3:Long):Long {
        if (zeroBased) {
	    if (rank != 4 || !containsInternal(i0,i1,i2,i3)) return -1;
            var offset:Long = i0;
            offset = offset*(max1 + 1) + i1;
            offset = offset*(max2 + 1) + i2;
            offset = offset*(max3 + 1) + i3;
            return offset;
        } else { 
	    if (rank != 4 || !containsInternal(i0,i1,i2,i3)) return -1;
            var offset:Long = i0 - min0;
            offset = offset*(max1 - min1 + 1) + i1 - min1;
            offset = offset*(max2 - min2 + 1) + i2 - min2;
            offset = offset*(max3 - min3 + 1) + i3 - min3;
            return offset;
        }
    }


    public def min(i:Long):Long {
        if (i == 0) return min0;
        if (i == 1) return min1;
        if (i == 2) return min2;
        if (i == 3) return min3;
        if (i<0 || i>=rank) throw new ArrayIndexOutOfBoundsException("min: "+i+" is not a valid rank for "+this);
	return mins(i);
    }

    public def max(i:Long):Long {
        if (i == 0) return max0;
        if (i == 1) return max1;
        if (i == 2) return max2;
        if (i == 3) return max3;
        if (i<0 || i>=rank) throw new ArrayIndexOutOfBoundsException("max: "+i+" is not a valid rank for "+this);
        return maxs(i);
    }


    //
    // region operations
    //

    protected def computeBoundingBox():Region(rank)=this; 
    
    public def min():(Long)=>Long = (i:Long)=> min(i);
    public def max():(Long)=>Long = (i:Long)=> max(i);

    public def contains(that:Region(rank)): Boolean {
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

    public def contains(p:Point):Boolean {
        if (p.rank != rank) return false;
	// NOTE: intentional fall through of cases!
        switch((p.rank-1) as Int) {
           default:
               for (r in (p.rank-1)..4) {
                   if (p(r)<mins(r) || p(r)>maxs(r)) return false;
               }
           case 3n: { val tmp = p(3); if (tmp<min3 || tmp>max3) return false; }
           case 2n: { val tmp = p(2); if (tmp<min2 || tmp>max2) return false; }
           case 1n: { val tmp = p(1); if (tmp<min1 || tmp>max1) return false; }
           case 0n: { val tmp = p(0); if (tmp<min0 || tmp>max0) return false; }
        }
	return true;
    }

    public def contains(i0:Long){rank==1}:Boolean = containsInternal(i0);
    public def contains(i0:Long, i1:Long){rank==2}:Boolean = containsInternal(i0,i1);
    public def contains(i0:Long, i1:Long, i2:Long){rank==3}:Boolean = containsInternal(i0,i1,i2);
    public def contains(i0:Long, i1:Long, i2:Long, i3:Long){rank==4}:Boolean = containsInternal(i0,i1,i2,i3);

    private def containsInternal(i0:Long):Boolean {
        return i0>=min0 && i0<=max0;
    }

    private def containsInternal(i0:Long, i1:Long):Boolean { 
        if (CompilerFlags.useUnsigned() && zeroBased) {
            return ((i0 as ULong) <= (max0 as ULong)) &&
                   ((i1 as ULong) <= (max1 as ULong));
        } else {
            return i0>=min0 && i0<=max0 && 
                   i1>=min1 && i1<=max1;
        }
    }

    private def containsInternal(i0:Long, i1:Long, i2:Long):Boolean {
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

    private def containsInternal(i0:Long, i1:Long, i2:Long, i3:Long):Boolean {
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
            polyRep = Region.makeRectangularPoly(new Rail[Long](rank, (i:Long)=>min(i)), 
                                                 new Rail[Long](rank, (i:Long)=>max(i))) as Region(rank);
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
	    val newMin = new Rail[Long](rank, (i:Long)=>Math.max(min(i), thatMin(i)));
	    val newMax = new Rail[Long](rank, (i:Long)=>Math.min(max(i), thatMax(i)));
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
            val newMin = new Rail[Long](k, (i:Long)=>i<rank?min(i):thatMin(i-rank));
            val newMax = new Rail[Long](k, (i:Long)=>i<rank?max(i):thatMax(i-rank));
            return new RectRegion(newMin, newMax);
        } else if (that instanceof RectRegion1D) {
            val thatMin = that.min(0);
            val thatMax = that.max(0);
            val k = rank+1;
            val newMin = new Rail[Long](k, (i:Long)=>i<rank?min(i):thatMin);
            val newMax = new Rail[Long](k, (i:Long)=>i<rank?max(i):thatMax);
            return new RectRegion(newMin, newMax);
        } else if (that instanceof FullRegion) {
       	    val k = rank+that.rank;
            val newMin = new Rail[Long](k, (i:Long)=>i<rank?min(i):Long.MIN_VALUE);
            val newMax = new Rail[Long](k, (i:Long)=>i<rank?max(i):Long.MAX_VALUE);
	    return new RectRegion(newMin,newMax);
        } else {
	   return (toPolyRegion() as Region(rank)).product(that);
        }
    }

    public def translate(v:Point(rank)):Region(rank){self.rect} {
        val newMin = new Rail[Long](rank, (i:Long)=>min(i)+v(i));
        val newMax = new Rail[Long](rank, (i:Long)=>max(i)+v(i));
        return new RectRegion(newMin, newMax) as Region(rank){self.rect};
    }

    public def projection(axis:Long):Region(1){self.rect} {
        return new RectRegion(min(axis), max(axis));
    }

    public def eliminate(axis:Long):Region{self.rect} /*(rank-1)*/ {
    	val k = rank-1;
        val newMin = new Rail[Long](k, (i:Long)=>i<axis?min(i):min((i)+1));
        val newMax = new Rail[Long](k, (i:Long)=>i<axis?max(i):max((i)+1));
        return new RectRegion(newMin, newMax);
    }    


    private static class RRIterator(myRank:Long) implements Iterator[Point(myRank)] {
        val min:(Long)=>Long;
        val max:(Long)=>Long;
        var done:Boolean;
        val cur:Rail[Long]{self.size==myRank};

        def this(rr:RectRegion):RRIterator{self.myRank==rr.rank} {
            property(rr.rank);
	    val t = rr.min();
            min = t;
            max = rr.max();
            done = rr.size == 0;
            cur = new Rail[Long](myRank, (l:Long)=>t(l));
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
	            var carryRank:Long = myRank-2;
	            while (carryRank>0 && cur(carryRank) > max(carryRank)) {
                        cur(carryRank) = min(carryRank);
	                cur(carryRank-1n)++;
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


    public def equals(thatObj:Any):Boolean {
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
        for (i in 0..(rank-1)) {
            if (thisMin(i)!=thatMin(i) || thisMax(i)!=thatMax(i))
                return false;
        }
        return true;
    }

    public def toString():String {
        val thisMin = this.min();
        val thisMax = this.max();
        var s: String = "[";
        for (i in 0..(rank-1)) {
            if (i>0) s += ",";
            s += "" + thisMin(i) + ".." + thisMax(i);
        }
        s += "]";
        return s;
    }
}
