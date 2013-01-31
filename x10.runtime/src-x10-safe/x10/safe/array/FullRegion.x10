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
import safe.lang.Int;
import safe.lang.Boolean; 
import safe.array.Point;
import safe.array.Region; 
/**
 * A full region is the unbounded region that contains all points of its rank
 */
final class FullRegion extends Region{rect == Boolean.TRUE()} {

    def this(val rank:Int):FullRegion{self.rank==rank} {
        super(rank, Boolean.TRUE(), Boolean.FALSE());
	if (Boolean.asX10Boolean(rank<0)) 
		throw new IllegalArgumentException("Rank is negative ("+rank+")");
    }

    public def isConvex() = Boolean.TRUE();
    public def isEmpty() = Boolean.FALSE();
    public def size():Int {
        throw new UnboundedRegionException("size not supported");
    }
    public def indexOf(Point):Int {
        throw new UnboundedRegionException("indexOf not supported");
    }
    public def min():(Int)=>Int {
        return (i:Int) => {
            if (Boolean.asX10Boolean(i<0 || i>=rank))
            	throw new ArrayIndexOutOfBoundsException("min: "+i+" is not a valid rank for "+this);
            Int.MIN_VALUE()
        };
    }
    public def max():(Int)=>Int {
        return (i:Int) => {
            if (Boolean.asX10Boolean(i<0 || i>=rank))
            	throw new ArrayIndexOutOfBoundsException("max: "+i+" is not a valid rank for "+this);
            Int.MAX_VALUE()
        };
    }
    public def Intersection(that: Region(rank)): Region(rank) = that;
    // public def product(that: Region):Region{self != null} /*: Region(this.rank+that.rank)*/{
    //     if (Boolean.asX10Boolean(that.isEmpty())) {
    //         return Region.makeEmpty(rank+that.rank);
    //     } else if (that instanceof FullRegion) {
    //         return new FullRegion(rank+that.rank);
    //     } else if (that instanceof RectRegion) {
    //         val thatMin = (that as RectRegion).min();
    //         val thatMax = (that as RectRegion).max();
    //         val newRank = rank+that.rank;
    //         val newMin = new Array[Int](newRank, (i:Int)=>i<rank?Int.MIN_VALUE:thatMin(i-rank));
    //         val newMax = new Array[Int](newRank, (i:Int)=>i<rank?Int.MAX_VALUE:thatMax(i-rank));
    //         return new RectRegion(newMin,newMax);
    //     } else if (that instanceof RectRegion1D) {
    //         return this.product((that as RectRegion1D).toRectRegion());
    //     } else {
	   //  throw new UnsupportedOperationException("haven't implemented FullRegion product with "+that.typeName());
    //     }
    // }
    public def projection(axis: Int): Region(Int.asInt(1)) = new FullRegion(1);
    public def translate(p:Point(rank)): Region(rank) = this;
    public def eliminate(i:Int)= new FullRegion(rank-Int.asInt(1));
    protected def computeBoundingBox(): Region(rank) = this;
    public def contains(that: Region(rank)):Boolean = true;
    public def contains(p:Point):Boolean = true;
    public def toString() = "full(" + rank + ")";


    public def iterator():Iterator[Point(rank)] {
        throw new UnboundedRegionException("iterator not supported");
    }
}
