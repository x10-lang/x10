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


/**
 * A RectRegion is a finite dense rectangular region with a specified rank.
 * This class implements a specialization of PolyRegion.
 */
public final class RectRegion extends Region{rect} {

    private val size:int;
    private val mins:ValRail[int]; /* will be null if rank<5 */
    private val maxs:ValRail[int]; /* will be null if rank<5 */

    private val min0:int;
    private val min1:int;
    private val min2:int;
    private val min3:int;
    private val max0:int;
    private val max1:int;
    private val max2:int;
    private val max3:int;

    private val myMin:(int)=>int;
    private val myMax:(int)=>int;

    private static def allZeros(x:ValRail[int]) {
       for (i in x) if (i != 0) return false;
       return true;
    }

    /**
     * Create a rectangular region containing all points p such that min <= p and p <= max.
     */
    def this(minArg:ValRail[int], maxArg:ValRail[int](minArg.length)):RectRegion(minArg.length) {
        super(minArg.length, true, allZeros(minArg));

        var s:int = 1;
        for (var i:int = 0; i<minArg.length; i++) {
	    var rs:int = maxArg(i) - minArg(i) + 1;
	    if (rs < 0) rs = 0;
            s *= rs;
        }
        size = s;

        if (minArg.length>0) {
            min0 = minArg(0);
            max0 = maxArg(0);
        } else {
            min0 = max0 = 0;
        }

        if (minArg.length>1) {
            min1 = minArg(1);
            max1 = maxArg(1);
        } else {
            min1 = max1 = 0;
        }

        if (minArg.length>2) {
            min2 = minArg(2);
            max2 = maxArg(2);
        } else {
            min2 = max2 = 0;
        }

        if (minArg.length>3) {
            min3 = minArg(3);
            max3 = maxArg(3);
        } else {
            min3 = max3 = 0;
        }
	
	if (minArg.length>4) {
	  mins = minArg;
	  maxs = maxArg;
        } else {
	  mins = null;
          maxs = null;
        }
	 myMin = (i:int) => {
	        switch(i) {
	           case 0: return min0;
	           case 1: return min1;
	           case 2: return min2;
	           case 3: return min3;
	           default: return mins(i);
	        }
	    };

	  myMax = (i:int) => {
	        switch(i) {
	        case 0: return max0;
	        case 1: return max1;
	        case 2: return max2;
	        case 3: return max3;
	        default: return maxs(i);
	        }
	    };
    }

    /**
     * Create a 1-dim region min..max.
     */
    def this(min:int, max:int):RectRegion(1){
        this([min],[max]);
    }

    public def size() = size;

    public def isConvex() = true;

    public def isEmpty() = size == 0;

    public def indexOf(pt:Point) {
	if (pt.rank != rank) return -1;
        var offset: int = pt(0) - min(0);
        for (var i:int=1; i<rank; i++) {
            val delta_i = max(i) - min(i) + 1;
            offset = offset*delta_i + pt(i) - min(i);
        }
        return offset;
    }


    //
    // region operations
    //

    protected def computeBoundingBox(): RectRegion(rank)=this; 

    
    public def min():(int)=>int = myMin;
    public def max():(int)=>int = myMax;

    public def contains(that:Region(rank)): boolean {
       if (that instanceof RectRegion) {
           val thatMin = (that as RectRegion).min();
           val thatMax = (that as RectRegion).max();
           for (var i:int =0; i<rank; i++) {
               if (min(i) > thatMin(i)) return false;
               if (max(i) < thatMax(i)) return false;
           }
           return true;
       } else {
           return this.contains(that.computeBoundingBox());
       }
    }

    public def contains(p:Point):boolean {
        if (p.rank != rank) return false;
	// NOTE: intentional fall through of cases!
        switch(p.rank-1) {
           default:
               for ([r] in p.rank-1..4) {
                   if (p(r)<mins(r) || p(r)>maxs(r)) return false;
               }
           case 3: { val tmp = p(3); if (tmp<min3 || tmp>max3) return false; }
           case 2: { val tmp = p(2); if (tmp<min2 || tmp>max2) return false; }
           case 1: { val tmp = p(1); if (tmp<min1 || tmp>max1) return false; }
           case 0: { val tmp = p(0); if (tmp<min0 || tmp>max0) return false; }
        }
	return true;
    }

    public def contains(i0:int){rank==1}:boolean {
        return i0>=min0 && i0<=max0;
    }

    public def contains(i0:int, i1:int){rank==2}:boolean { 
        if (zeroBased) {
            return ((i0 as UInt) <= (max0 as UInt)) &&
                   ((i1 as UInt) <= (max1 as UInt));
        } else {
            return i0>=min0 && i0<=max0 && 
                   i1>=min1 && i1<=max1;
        }
    }

    public def contains(i0:int, i1:int, i2:int){rank==3}:boolean {
        if (zeroBased) {
            return ((i0 as UInt) <= (max0 as UInt)) &&
                   ((i1 as UInt) <= (max1 as UInt)) &&
                   ((i2 as UInt) <= (max2 as UInt));
        } else {
            return i0>=min0 && i0<=max0 && 
                   i1>=min1 && i1<=max1 && 
                   i2>=min2 && i2<=max2;
        }
    }

    public def contains(i0:int, i1:int, i2:int, i3:int){rank==4}:boolean {
        if (zeroBased) {
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


    transient protected var polyRep:Region(rank)=null;
    /**
     * Return a PolyRegion with the same set of points as this region. This permits
     * general algorithms for intersection, restriction etc to be applied to RectRegion's.
     */
    public def toPolyRegion() {
    	if (polyRep==null) {
    		polyRep = Region.makeRectangularPoly(ValRail.make[int](rank, min()), ValRail.make[int](rank, max()));
    	}
    	return polyRep;
    }
    
    
    public def intersection(that: Region(rank)):Region(rank) {
        if (that.isEmpty()) {
	       return that;
        } else if (that instanceof FullRegion) {
            return this;
        } else if (that instanceof RectRegion) {
            val thatMin = (that as RectRegion).min();
            val thatMax = (that as RectRegion).max();
	        val newMin = ValRail.make[int](rank, (i:int)=>Math.max(min(i), thatMin(i)));
	        val newMax = ValRail.make[int](rank, (i:int)=>Math.min(max(i), thatMax(i)));
	        for ([i] in 0..newMin.length-1) {
                if (newMax(i)<newMin(i)) return Region.makeEmpty(rank);
            }
            return new RectRegion(newMin, newMax);
        } else {
        	// Use the general representation.
        	return (toPolyRegion() as Region(rank)).intersection(that);
        }
    }
    

    
    public def product(that:Region):Region /*self.rank==this.rank+that.rank*/{
        if (that.isEmpty()) {
            return Region.makeEmpty(rank + that.rank);
        } else if (that instanceof RectRegion) {
            val thatMin = (that as RectRegion).min();
            val thatMax = (that as RectRegion).max();
            val k = rank+that.rank;
            val newMin = ValRail.make[int](k, (i:int)=>i<rank?min(i):thatMin(i-rank));
            val newMax = ValRail.make[int](k, (i:int)=>i<rank?max(i):thatMax(i-rank));
            return new RectRegion(newMin, newMax);
        } else if (that instanceof FullRegion) {
        	val k = rank+that.rank;
            val newMin = ValRail.make[int](k, (i:int)=>i<rank?min(i):Int.MIN_VALUE);
            val newMax = ValRail.make[int](k, (i:int)=>i<rank?max(i):Int.MAX_VALUE);
	    return new RectRegion(newMin,newMax);
        } else {
	   return (toPolyRegion() as Region(rank)).product(that);
        }
    }

    public def translate(v: Point(rank)): Region(rank){self.rect} {
        val newMin = ValRail.make[int](rank, (i:int)=>min(i)+v(i));
        val newMax = ValRail.make[int](rank, (i:int)=>max(i)+v(i));
        return new RectRegion(newMin, newMax);
    }

    public def projection(axis: int):Region(1){self.rect} {
        return new RectRegion([min(axis)], [max(axis)]);
    }

    public def eliminate(axis: int):Region{self.rect} /*(rank-1)*/ {
    	val k = rank-1;
        val newMin = ValRail.make[int](k, (i:int)=>i<axis?min(i):min(i+i));
        val newMax = ValRail.make[int](k, (i:int)=>i<axis?max(i):max(i+i));
        return new RectRegion(newMin, newMax);
    }    


    private static class RRIterator(myRank:int) implements Iterator[Point(myRank)]() {
        val min:(int)=>int;
        val max:(int)=>int;
        var done:boolean;
        val cur:Rail[int](myRank);

        def this(rr:RectRegion):RRIterator{self.myRank==rr.rank} {
            property(rr.rank);
            min = rr.min();
            max = rr.max();
            done = rr.size == 0;
            cur = Rail.make[int](myRank, min);
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


    public def equals(thatObj:Any): boolean {
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

    public def toString(): String {
        val thisMin = this.min();
        val thisMax = this.max();
        var s: String = "[";
        for (var i: int = 0; i<rank; i++) {
            if (i>0) s += ",";
            s += thisMin(i) + ".." + thisMax(i);
        }
        s += "]";
        return s;
    }

    /**
     * The Scanner class supports efficient scanning. Usage:
     *
     *    for (s:Scanner in r.scanners()) {
     *        int min0 = s.min(0);
     *        int max0 = s.max(0);
     *        for (var i0:int=min0; i0<=max0; i0++) {
     *            s.set(0,i0);
     *            int min1 = s.min(1);
     *            int max1 = s.max(1);
     *            for (var i1:int=min1; i1<=max1; i1++) {
     *                ...
     *            }
     *        }
     *    }
     *
     */

    private class RectRegionScanner implements Region.Scanner {
    	var axis:Int=0;
        def this(){}
        public def set(axis:int, position: int) {
        	// ???
        }
        public def min(axis:int):int = RectRegion.this.min()(axis);
        public def max(axis:int):int = RectRegion.this.max()(axis);
    }
    private class Scanners implements Iterator[Region.Scanner] {

        var hasNext: boolean = true;

        public def hasNext(): boolean {
            return hasNext;
        }

        public def next(): Region.Scanner {
            if (hasNext) {
                hasNext = false;
                return new RectRegionScanner();
            } else
                throw new x10.util.NoSuchElementException("in scanner");
        }

        public def remove(): void {
            throw new UnsupportedOperationException("remove");
        }
    }

    public def scanners()=new Scanners();
  

}
