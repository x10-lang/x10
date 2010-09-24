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
 * This class is an highly optimized implementation for a
 * the "unique" distribution that maps the region [0..Place.MAX_PLACES-1]
 * such that for every place this.get(p) == [p..p].<p>
 */
class UniqueDist extends Dist(1){rect} {
   
    /**
     * Cached restricted region for the current place.
     */
    private transient var regionForHere:Region(this.rank);


    def this():UniqueDist{self.rank==1,self.rect,self.unique} {
	super([0..Place.MAX_PLACES-1], true, false, Place(0));
    }


    public def places():ValRail[Place] {
        return Place.places;
    }

    public def regions():ValRail[Region(rank)] {
	return	ValRail.make[Region(rank)](Place.MAX_PLACES, (i:int)=>[i..i] as Region(rank));
    }

    public def get(p:Place):Region(rank) {
        if (p == here) {
            if (regionForHere == null) {
                regionForHere = [here.id..here.id];
            }
	    return regionForHere;
        } else {
            return [p.id..p.id];
        }
    }

    public def apply(p:Place):Region(rank) = get(p);


    // unchecked assumption that ranks match.  Will add rank constraint shortly
    public def apply(pt:Point):Place {
	return Place.places(pt(0));
    }

    // unchecked assumption that ranks match.  Will add rank constraint shortly
    public def apply(i0:int) {
	return Place.places(i0);
    }

    public def restriction(r:Region(rank)):Dist(rank) {
	return new WrappedDistRegionRestricted(this, r) as Dist(rank); // TODO: cast should not be needed
    }

    public def restriction(p:Place):Dist(rank) {
	return new WrappedDistPlaceRestricted(this, p) as Dist(rank); // TODO: cast should not be needed
    }

    public def equals(thatObj:Any):boolean {
	return thatObj instanceof UniqueDist;
    }
}

