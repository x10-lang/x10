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
public final class RectRegion extends BaseRegion{rect} {

    global private val size:int;
    global private val mins:ValRail[int];
    global private val maxs:ValRail[int];

    private static def allZeros(x:ValRail[int]) {
       for (i in x) if (i != 0) return false;
       return true;
    }

    private def this(minArg:ValRail[int], maxArg:ValRail[int]):RectRegion{self.rank==minArg.length} {
        super(minArg.length, true, allZeros(minArg));

        var s:int = 1;
        for (var i:int = 0; i<minArg.length; i++) {
            s *= maxArg(i) - minArg(i) + 1;
        }
        size = s;

	mins = minArg;
	maxs = maxArg;
    }

    public static def make1(min:Rail[int]!, max:Rail[int]!):RectRegion{self.rank==min.length,self.rect} {
        if (max.length!=max.length) 
            throw U.illegal("min and max must have same length");

        return new RectRegion(min as ValRail[int], max as ValRail[int]);
    }


    public static def make1(min:int, max:int): Region{self.rect,self.rank==1} {
	return new RectRegion([min], [max]);
    }

    public global def size() = size;

    public global def isConvex() = true;

    public global def isEmpty() = size == 0;


    //
    // specialized bounds checking for performance
    // 

    global def check(err:(Point)=>RuntimeException, i0: int) {rank==1} {
        if (i0<min(0) || i0>max(0)) {
            throw err(Point.make(i0));
        }
    }

    global def check(err:(Point)=>RuntimeException, i0: int, i1: int) {rank==2} {
        if (i0<min(0) || i0>max(0) ||
            i1<min(1) || i1>max(1)) {
            throw err(Point.make(i0,i1));
        }
    }

    global def check(err:(Point)=>RuntimeException, i0: int, i1: int, i2: int) {rank==3} {
        if (i0<min(0) || i0>max(0) ||
            i1<min(1) || i1>max(1) ||
            i2<min(2) || i2>max(2)) {
            throw err(Point.make(i0,i1,i2));
        }
    }

    global def check(err:(Point)=>RuntimeException, i0: int, i1: int, i2: int, i3: int) {rank==4} {
        if (i0<min(0) || i0>max(0) ||
            i1<min(1) || i1>max(1) ||
            i2<min(2) || i2>max(2) ||
            i3<min(3) || i3>max(3)) {
            throw err([i0,i1,i2,i3] as Point);
        }
    }


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
            return new RectRegion(newMin, newMax);
        } else {
	    throw U.unsupported("haven't implemented RectRegion intersection with "+that.typeName());
        }
    }
    

    public global def product(that:Region):Region {
        if (that.isEmpty()) {
            return Region.makeEmpty(rank + that.rank);
        } else if (that instanceof RectRegion) {
            val thatMin = (that as RectRegion).min();
            val thatMax = (that as RectRegion).max();
            val newMin = ValRail.make[int](rank+that.rank, (i:int)=>i<rank?min(i):thatMin(i-rank));
            val newMax = ValRail.make[int](rank+that.rank, (i:int)=>i<rank?max(i):thatMax(i-rank));
            return new RectRegion(newMin, newMax);
        } else if (that instanceof FullRegion) {
            val newMin = ValRail.make[int](rank+that.rank, (i:int)=>i<rank?min(i):Int.MIN_VALUE);
            val newMax = ValRail.make[int](rank+that.rank, (i:int)=>i<rank?max(i):Int.MAX_VALUE);
	    return new RectRegion(newMin,newMax);
        } else {
	    throw U.unsupported("haven't implemented RectRegion product with "+that.typeName());
        }
    }

    public global def translate(v: Point(rank)): Region(rank) {
        val newMin = ValRail.make[int](rank, (i:int)=>min(i)+v(i));
        val newMax = ValRail.make[int](rank, (i:int)=>max(i)+v(i));
        return new RectRegion(newMin, newMax);
    }

    public global def projection(axis: int): Region(1) {
        return new RectRegion([min(axis)], [max(axis)]);
    }

    public global def eliminate(axis: int):Region /*(rank-1)*/ {
        val newMin = ValRail.make[int](rank-1, (i:int)=>i<axis?min(i):min(i+i));
        val newMax = ValRail.make[int](rank-1, (i:int)=>i<axis?max(i):max(i+i));
        return new RectRegion(newMin, newMax);
    }    


    private static class RRIterator(myRank:int) implements Iterator[Point(myRank)]() {
        val min:ValRail[int](myRank);
        val max:ValRail[int](myRank);
        var done:boolean;
        val cur:Rail[int](myRank)!;

        def this(min_:ValRail[int], max_:ValRail[int], r:int):RRIterator{self.myRank==r} {
            property(r);
            min = min_;
            max = max_;
            done = false;
            cur = min_ as Rail[int];
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
        return new RRIterator(mins, maxs, rank) as Iterator[Point(rank)];
    }


    public global safe def equals(thatObj:Any): boolean {
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

}
