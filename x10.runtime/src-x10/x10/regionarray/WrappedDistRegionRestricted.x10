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
/**
 * This class wraps another distribution to restrict the region
 * to a subset of the original dist's region.
 */
final class WrappedDistRegionRestricted extends Dist {
    val base:Dist(rank);
    val filter:Region(rank);

    def this(d:Dist, r:Region(d.rank)):WrappedDistRegionRestricted(d.rank) {
        super(d.region.intersection(r));
        base = d;  
        filter = r;  
    }

    public def places():PlaceGroup {
        return base.places();
    }

    public def numPlaces() = base.numPlaces();

    public def regions():Iterable[Region(rank)] {
        val pg = base.places();
        return new Rail[Region(rank)](pg.numPlaces(),
                                      (i:Long)=>base.get(pg(i)).intersection(filter));
    }

    public def get(p:Place):Region(rank) {
        return base.get(p).intersection(filter);
    }

    // replicated from superclass to workaround xlC bug with using & itables
    public operator this(p:Place):Region(rank) = get(p);

    public operator this(pt:Point(rank)):Place {
        if (filter.contains(pt)) {
            return base(pt);
        } else {
            throw new ArrayIndexOutOfBoundsException("point " + pt + " not contained in distribution");
        }
    }

    // replicated from superclass to workaround xlC bug with using & itables
    public operator this(i0:Long){rank==1}:Place = this(Point.make(i0));

    // replicated from superclass to workaround xlC bug with using & itables
    public operator this(i0:Long, i1:Long){rank==2}:Place = this(Point.make(i0, i1));

    // replicated from superclass to workaround xlC bug with using & itables
    public operator this(i0:Long, i1:Long, i2:Long){rank==3}:Place = this(Point.make(i0, i1, i2));

    // replicated from superclass to workaround xlC bug with using & itables
    public operator this(i0:Long, i1:Long, i2:Long, i3:Long){rank==4}:Place = this(Point.make(i0,i1,i2,i3));

    public def offset(pt:Point(rank)):Long {
        if (filter.contains(pt)) {
            return base.offset(pt);
        } else {
            throw new ArrayIndexOutOfBoundsException("point " + pt + " not contained in distribution");
        }
    }

    public def maxOffset():Long = base.maxOffset();

    public def restriction(r:Region(rank)):Dist(rank) {
        return new WrappedDistRegionRestricted(base, filter.intersection(r)); 
    }

    public def restriction(p:Place):Dist(rank) {
        return new WrappedDistPlaceRestricted(this, p);
    }

    public def equals(thatObj:Any):Boolean {
	if (!(thatObj instanceof WrappedDistRegionRestricted)) return false;
        val that = thatObj as WrappedDistRegionRestricted;
	return this.base.equals(that.base) && this.filter.equals(that.filter);
    }
}
public type WrappedDistRegionRestricted(r:Long) = WrappedDistRegionRestricted{self.rank==r};
