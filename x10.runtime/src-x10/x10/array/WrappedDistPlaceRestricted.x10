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
 * This class wraps another distribution to restrict the region
 * to a subset of the original dist's region.
 */
final class WrappedDistPlaceRestricted extends Dist {
    val base:Dist{self.rank==this.rank};
    val filter:Place;

    def this(d:Dist, p:Place):WrappedDistPlaceRestricted{this.rank==d.rank} {
        super(d.get(p));
	base = d as Dist{self.rank==this.rank}; // cast should not be needed
        filter = p;
    }

    public def places():PlaceGroup = new SparsePlaceGroup(filter);

    public def numPlaces() = 1;

    public def regions():Sequence[Region(rank)] {
        return new Array[Region(rank)](1, (int)=>region).sequence();
    }

    public def get(p:Place):Region(rank) {
	if (p.equals(filter)) {
            return region;
        } else {
            return Region.makeEmpty(rank);
        }
    }

    public operator this(pt:Point(rank)):Place {
	val bp = base(pt);
	if (bp.equals(filter)) {
	    return bp;
        } else {
            throw new ArrayIndexOutOfBoundsException("point " + pt + " not contained in distribution");
        }
    }

    public def offset(pt:Point(rank)):int {
        if (here == filter) {
            return base.offset(pt);
       } else {
            throw new ArrayIndexOutOfBoundsException("point " + pt + " not contained in distribution");
        }
    }

    public def maxOffset():int = base.maxOffset();

    public def restriction(r:Region(rank)):Dist(rank) {
        return new WrappedDistRegionRestricted(this, r) as Dist(rank); // TODO cast should not be needed
    }

    public def restriction(p:Place):Dist(rank) {
	if (p.equals(here)) {
            return this;
        } else {
            return Dist.make(Region.makeEmpty(rank));
        }
    }

    public def equals(thatObj:Any):boolean {
	if (!(thatObj instanceof WrappedDistPlaceRestricted)) return false;
        val that = thatObj as WrappedDistPlaceRestricted;
	return this.base.equals(that.base) && this.filter.equals(that.filter);
    }
}
