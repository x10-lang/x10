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
 * A RectRegion is a finite, rank-dimensional, dense rectangular region.
 */
public final class RectRegion extends Region{rect} {

    global private val size:int;
    global private val mins:ValRail[int];
    global private val maxs:ValRail[int];

    // Cached contents of the min/max ValRails
    // to avoid loads & bounds checks when accessing
    // Critical for performance because this is on the fastpath
    // of most RectRegion operations.
    global private val min0:int;
    global private val min1:int;
    global private val min2:int;
    global private val min3:int;
    global private val max0:int;
    global private val max1:int;
    global private val max2:int;
    global private val max3:int;

    private static def allZeros(x:ValRail[int]) {
       for (i in x) if (i != 0) return false;
       return true;
    }

    def this(minArg:ValRail[int], maxArg:ValRail[int](minArg.length)):RectRegion{self.rank==minArg.length} {
        super(minArg.length, true, allZeros(minArg));

        if (minArg.length!=maxArg.length) 
            throw new IllegalOperationException("min and max must have same length");

        var s:int = 1;
        for (var i:int = 0; i<minArg.length; i++) {
	    var rs:int = maxArg(i) - minArg(i) + 1;
	    if (rs < 0) rs = 0;
            s *= rs;
        }
        size = s;

	mins = minArg;
	maxs = maxArg;

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
    }

    def this(min:int, max:int):RectRegion{self.rank==1} {
        this([min],[max]);
    }

    public global def size() = size;

    public global def isConvex() = true;

    public global def isEmpty() = size == 0;


    //
    // region operations
    //

    protected global def computeBoundingBox(): Region(rank){self.rect}=this; 

    public global def min() = mins;
    public global def max() = maxs;

    public global def contains(that:Region(rank)): boolean {
       if (that instanceof RectRegion) {
            val thatMin = (that as RectRegion).min();
            val thatMax = (that as RectRegion).max();
           for (var i:int =0; i<rank; i++) {
               if (mins(i) > thatMin(i)) return false;
               if (maxs(i) < thatMax(i)) return false;
           }
           return true;
       } else {
           return this.contains(that.computeBoundingBox());
       }
    }

    public global def contains(p:Point):boolean {
        if (p.rank != rank) return false;
        for ((r) in 0..p.rank-1) {
            if (p(r)<mins(r) || p(r)>maxs(r)) return false;
        }
        return true;
    }

    public global def contains(i0:int){rank==1}:boolean {
        return i0>=min0 && i0<=max0;
    }

    public global def contains(i0:int, i1:int){rank==2}:boolean { 
        if (zeroBased) {
            return ((i0 as UInt) <= (max0 as UInt)) &&
                   ((i1 as UInt) <= (max1 as UInt));
        } else {
            return i0>=min0 && i0<=max0 && 
                   i1>=min1 && i1<=max1;
        }
    }

    public global def contains(i0:int, i1:int, i2:int){rank==3}:boolean {
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

    public global def contains(i0:int, i1:int, i2:int, i3:int){rank==4}:boolean {
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


    public global def intersection(that: Region(rank)):Region(rank) {
        if (that.isEmpty()) {
	    return that;
        } else if (that instanceof FullRegion) {
            return this;
        } else if (that instanceof RectRegion) {
            val thatMin = (that as RectRegion).min();
            val thatMax = (that as RectRegion).max();
	    val newMin = ValRail.make[int](rank, (i:int)=>Math.max(min(i), thatMin(i)));
	    val newMax = ValRail.make[int](rank, (i:int)=>Math.min(max(i), thatMax(i)));
	    for ((i) in 0..newMin.length-1) {
                if (newMax(i)<newMin(i)) return Region.makeEmpty(rank);
            }
            return new RectRegion(newMin, newMax);
        } else {
	    throw new UnsupportedOperationException("haven't implemented RectRegion intersection with "+that.typeName());
        }
    }
    

    public global def product(that:Region):Region /*self.rank==this.rank+that.rank*/{
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
	    throw new UnsupportedOperationException("haven't implemented RectRegion product with "+that.typeName());
        }
    }

    public global def translate(v: Point(rank)): Region(rank){self.rect} {
        val newMin = ValRail.make[int](rank, (i:int)=>min(i)+v(i));
        val newMax = ValRail.make[int](rank, (i:int)=>max(i)+v(i));
        return new RectRegion(newMin, newMax);
    }

    public global def projection(axis: int):Region(1){self.rect} {
        return new RectRegion([min(axis)], [max(axis)]);
    }

    public global def eliminate(axis: int):Region{self.rect} /*(rank-1)*/ {
    	val k = rank-1;
        val newMin = ValRail.make[int](k, (i:int)=>i<axis?min(i):min(i+i));
        val newMax = ValRail.make[int](k, (i:int)=>i<axis?max(i):max(i+i));
        return new RectRegion(newMin, newMax);
    }    


    private static class RRIterator(myRank:int) implements Iterator[Point(myRank)]() {
        val min:ValRail[int](myRank);
        val max:ValRail[int](myRank);
        var done:boolean;
        val cur:Rail[int](myRank)!;

        def this(rr:RectRegion):RRIterator{self.myRank==rr.rank} {
            property(rr.rank);
            min = rr.mins as ValRail[int](myRank);
            max = rr.maxs as ValRail[int](myRank);
            done = rr.size == 0;
            cur = rr.mins as Rail[int](myRank)!;
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
    public global def iterator():Iterator[Point(rank)] {
        return new RRIterator(this);
    }


    public global safe def equals(thatObj:Any): boolean {
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


    //
    //
    //

    public global safe def toString(): String {
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


    public global def scanners():Iterator[Region.Scanner]! {
        throw new UnsupportedOperationException("TODO: scanners not defined for RectRegion");
    }


}
