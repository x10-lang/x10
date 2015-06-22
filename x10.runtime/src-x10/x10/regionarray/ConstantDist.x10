/*
 *  This file is part of the X10 project (http://x10-lang.org).
 * 
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 * 
 *  (C) Copyright IBM Corporation 2006-2015.
 *  (C) Copyright Australian National University 2010.
 */

package x10.regionarray;

import x10.compiler.CompilerFlags;

/**
 * This class is an optimized implementation for a
 * distribution that maps all points in its region
 * to a single place.<p>
 */
final class ConstantDist(onePlace:Place) extends Dist {
    
    /**
     * Create a distribution that maps every point in r to p
     */
    def this(r:Region, p:Place) {
        super(r);
        property(p);
    }
    
    public def places():PlaceGroup = new SparsePlaceGroup(onePlace);
    
    public def numPlaces():Long = 1;
    
    public def regions():Iterable[Region(rank)] {
        return new Rail[Region(rank)](1, region);
    }
    
    public def get(p:Place):Region(rank) {
        if (p == onePlace) {
            return region;
        } else {
            return Region.makeEmpty(rank);
        }
    }
    
    // replicated from superclass to workaround xlC bug with using & itables
    public operator this(p:Place):Region(rank) = get(p);

    public operator this(pt:Point(rank)):Place {
        if (CompilerFlags.checkBounds() && !region.contains(pt)) raiseBoundsError(pt);
        return onePlace;
    }
    
    public operator this(i0:Long){rank==1}:Place {
        if (CompilerFlags.checkBounds() && !region.contains(i0)) raiseBoundsError(i0);
        return onePlace;
    }
    
    public operator this(i0:Long, i1:Long){rank==2}:Place {
        if (CompilerFlags.checkBounds() && !region.contains(i0, i1)) raiseBoundsError(i0,i1);
        return onePlace;
    }
    
    public operator this(i0:Long, i1:Long, i2:Long){rank==3}:Place {
        if (CompilerFlags.checkBounds() && !region.contains(i0, i1, i2)) raiseBoundsError(i0,i1,i2);
        return onePlace;
    }
    
    public operator this(i0:Long, i1:Long, i2:Long, i3:Long){rank==4}:Place {
        if (CompilerFlags.checkBounds() && !region.contains(i0, i1, i2, i3)) raiseBoundsError(i0,i1,i2,i3);
        return onePlace;
    }
    
    public def restriction(r:Region(rank)):Dist(rank) {
        return new WrappedDistRegionRestricted(this, r);
    }
    
    public def restriction(p:Place):Dist(rank) {
        return new WrappedDistPlaceRestricted(this, p);
    }
    
    public def equals(thatObj:Any):Boolean {
        if (!(thatObj instanceof ConstantDist)) return false;
        val that = thatObj as ConstantDist;
        return this.onePlace.equals(that.onePlace) && this.region.equals(that.region);
    }
}

