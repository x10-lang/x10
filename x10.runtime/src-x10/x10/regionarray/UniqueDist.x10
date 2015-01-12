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

/**
 * A distribution that maps the region 0..(numPlaces()-1)
 * to the members of its PlaceGroup such that for 
 * every Place p the region returned by get is 
 * the single point region that matches the indexOf
 * p in the PlaceGroup.
 * In particular, if the PlaceGroup of the UniqueDist 
 * is Place.places() and no Place has failed, then each Place p 
 * will be assigned the region p.id..p.id.
 */
final class UniqueDist extends Dist/*(1)*/ {

   /**
    * The place group for this distribution
    */
   private val pg:PlaceGroup;
   
    /**
     * Cached restricted region for the current place.
     */
    private transient var regionForHere:Region(this.rank);

    /**
     * Create a unique distribution over the argument PlaceGroup
     * @param g the place group
     */
    def this(g:PlaceGroup) {
        super(Region.make(0, (g.numPlaces()-1)));
        pg = g;
    }

    /**
     * Create a unique distribution over Place.places();
     */
    def this() {
        this(Place.places());
    }

    public def places():PlaceGroup = pg;

    public def numPlaces():Long = pg.numPlaces();

    public def regions():Iterable[Region(rank)] {
	return new Rail[Region(rank)](pg.numPlaces(), (i:Long):Region(rank)=>(Region.make(i,i) as Region(rank)));
    }

    public def get(p:Place):Region(rank) {
        if (p == here) {
            if (regionForHere == null) {
                val idx = pg.indexOf(here);
                regionForHere = Region.make(idx, idx) as Region(rank);
            }
	    return regionForHere;
        } else {
            val idx = pg.indexOf(p);
            return Region.make(idx, idx) as Region(rank);
        }
    }

    // replicated from superclass to workaround xlC bug with using & itables
    public operator this(p:Place):Region(rank) = get(p);

    public operator this(pt:Point(rank)):Place = pg(pt(0n));

    public operator this(i0:Long){rank==1}:Place = pg(i0);

    public def offset(pt:Point(rank)):Long {
        if (CompilerFlags.checkBounds() && !(pt(0n) >= 0 && pt(0n) < pg.numPlaces())) {
            raiseBoundsError(pt);
        }
        if (CompilerFlags.checkPlace() && pt(0n) != (pg.indexOf(here) as Long)) raisePlaceError(pt);
        return 0;
    }

    public def offset(i0:Long){rank==1}:Long {
        if (CompilerFlags.checkBounds() && !(i0 >= 0 && i0 < numPlaces())) {
            raiseBoundsError(i0);
        }
        if (CompilerFlags.checkPlace() && i0 != (pg.indexOf(here) as Long)) raisePlaceError(i0);
        return 0;
    }

    // replicated from superclass to workaround xlC bug with using & itables
    // This code is completely unreachable
    public operator this(i0:Long, i1:Long){rank==2}:Place {
        throw new UnsupportedOperationException("operator(i0:Long,i1:Long)");
    }

    // replicated from superclass to workaround xlC bug with using & itables
    // This code is completely unreachable
    public operator this(i0:Long, i1:Long, i2:Long){rank==3}:Place {
        throw new UnsupportedOperationException("operator(i0:Long,i1:Long,i2:Long)");
    }

    // replicated from superclass to workaround xlC bug with using & itables
    // This code is completely unreachable
    public operator this(i0:Long, i1:Long, i2:Long, i3:Long){rank==4}:Place {
       throw new UnsupportedOperationException("operator(i0:Long,i1:Long,i2:Long,i3:Long)");
    }

    public def maxOffset():Long = 0;

    public def restriction(r:Region(rank)):Dist(rank) {
	return new WrappedDistRegionRestricted(this, r);
    }

    public def restriction(p:Place):Dist(rank) {
	return new WrappedDistPlaceRestricted(this, p) as Dist(rank);  
    }

    public def equals(thatObj:Any):Boolean {
        if (thatObj instanceof UniqueDist) {
            val that = thatObj as UniqueDist;
            return pg.equals(that.pg);
        } else {
            return super.equals(thatObj);
        }
    }
}

