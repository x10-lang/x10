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

/**
 * A full region is the unbounded region that contains all points of its rank
 */
final class FullRegion extends Region{rect} {

    def this(val rank:Long):FullRegion{self.rank==rank} {
        super(rank, true, false);
	if (rank<0) throw new IllegalArgumentException("Rank is negative ("+rank+")");
    }

    public def isConvex() = true;
    public def isEmpty() = false;
    public def size():Long {
        throw new UnboundedRegionException("size not supported");
    }
    public def indexOf(Point):Long {
        throw new UnboundedRegionException("indexOf not supported");
    }
    public def min():(Long)=>Long {
        return (i:Long) => {
            if (i<0 || i>=rank) throw new ArrayIndexOutOfBoundsException("min: "+i+" is not a valid rank for "+this);
            Long.MIN_VALUE
        };
    }
    public def max():(Long)=>Long {
        return (i:Long) => {
            if (i<0 || i>=rank) throw new ArrayIndexOutOfBoundsException("max: "+i+" is not a valid rank for "+this);
            Long.MAX_VALUE
        };
    }
    public def intersection(that: Region(rank)): Region(rank) = that;
    public def product(that: Region):Region{self != null} /*: Region(this.rank+that.rank)*/{
        if (that.isEmpty()) {
            return Region.makeEmpty(rank+that.rank);
        } else if (that instanceof FullRegion) {
            return new FullRegion(rank+that.rank);
        } else if (that instanceof RectRegion) {
            val thatMin = (that as RectRegion).min();
            val thatMax = (that as RectRegion).max();
            val newRank = rank+that.rank;
            val newMin = new Rail[Long](newRank, (i:Long)=>i<rank?Long.MIN_VALUE:thatMin((i)-rank));
            val newMax = new Rail[Long](newRank, (i:Long)=>i<rank?Long.MAX_VALUE:thatMax((i)-rank));
            return new RectRegion(newMin,newMax);
        } else if (that instanceof RectRegion1D) {
            return this.product((that as RectRegion1D).toRectRegion());
        } else {
	    throw new UnsupportedOperationException("haven't implemented FullRegion product with "+that.typeName());
        }
    }
    public def projection(axis:Long): Region(1) = new FullRegion(1);
    public def translate(p:Point(rank)): Region(rank) = this;
    public def eliminate(i:Long)= new FullRegion(rank-1);
    protected def computeBoundingBox(): Region(rank) = this;
    public def contains(that: Region(rank)):Boolean = true;
    public def contains(p:Point):Boolean = true;
    public def toString() = "full(" + rank + ")";


    public def iterator():Iterator[Point(rank)] {
        throw new UnboundedRegionException("iterator not supported");
    }
}
