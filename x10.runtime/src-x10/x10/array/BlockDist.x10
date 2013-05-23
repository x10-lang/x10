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

import x10.compiler.CompilerFlags;

/**
 * <p>A BlockDist divides the coordinates along one axis
 * of its Region in a block fashion and distributes them
 * as evenly as possible to the places in its PlaceGroup.</p>
 * 
 * It caches the region for the current place as a transient field.
 * This makes the initial access to this information somewhat slow,
 * but optimizes the wire-transfer size of the Dist object. 
 * This appears to be the appropriate tradeoff, since Dist objects
 * are frequently serialized and usually the restriction operation is 
 * applied to get the Region for here, not for other places.
 */
final class BlockDist extends Dist {
   
    /**
     * The place group for this distribution
     */
    private val pg:PlaceGroup;

    /**
     * The axis along which the region is being distributed
     */
    private val axis:int;

    /**
     * Cached restricted region for the current place.
     */
    private transient var regionForHere:Region(this.rank);


    def this(r:Region, axis:int, pg:PlaceGroup):BlockDist{self.region==r} {
        super(r);
        this.axis = axis;
        this.pg = pg;
    }


    /**
     * The key algorithm for this class.
     * Compute the region for the given place by doing region algebra.
     *
     * Assumption: Caller has done error checking to ensure that place is 
     *   actually a member of pg.
     */
    private def blockRegionForPlace(place:Place):Region{self.rank==this.rank} {
        val b = region.boundingBox();
        val min = b.min(axis);
        val max = b.max(axis);
        val P = pg.numPlaces() as long;
        val numElems = max - min + 1L;
        val blockSize = numElems/P;
        val leftOver = numElems - P*blockSize;
        val i = pg.indexOf(place) as long;
        val low = min + blockSize*i + (i< leftOver ? i : leftOver);
        val hi = low + blockSize + (i < leftOver ? 0 : -1);
        
        if (region instanceof RectRegion) {
            // Optimize common case.
            val newMin = new Rail[long](rank, (i:long) => region.min(i as int));
            val newMax = new Rail[long](rank, (i:long) => region.max(i as int));
            newMin(axis) = low;
            newMax(axis) = hi;
            return new RectRegion(newMin, newMax) as Region(rank);
        } else if (region instanceof RectRegion1D) {
            return new RectRegion1D(low, hi) as Region(rank);
        } else {
            // General case handled via region algebra
            val r1 = Region.makeFull(axis);
            val r2 = Region.make(low, hi);
            val r3 = Region.makeFull(region.rank-axis-1);
            return (r1.product(r2).product(r3) as Region(region.rank)).intersection(region);
        }
    }

    /**
     * Given an index into the "axis dimension" determine which place it 
     * is mapped to.
     *
     * Assumption: Caller has done error checking to ensure that index is 
     *   actually within the bounds of the axis dimension.
     */
    private def mapIndexToPlace(index:long):Place {
        val bb = region.boundingBox();
        val min = bb.min(axis);
        val max = bb.max(axis);
        val P = pg.numPlaces();
        val numElems = max - min + 1;
        val blockSize = numElems/P;
        val leftOver = numElems - P*blockSize;
        val normalizedIndex = index-min;
        
        val nominalPlace = normalizedIndex/(blockSize+1);
        if (nominalPlace < leftOver) {
            return pg(nominalPlace);
        } else {
            val indexFromTop = max-index;
            return pg(pg.numPlaces() - 1 - (indexFromTop/(blockSize)));
        }
    }
    
    
    public def places():PlaceGroup = pg;
    
    public def numPlaces():long = pg.numPlaces();

    public def regions():Iterable[Region(rank)] {
        return new Rail[Region(rank)](pg.numPlaces(), (i:long)=>blockRegionForPlace(pg(i)));
    }

    public def get(p:Place):Region(rank) {
        if (p == here) {
            if (regionForHere == null) {
                regionForHere = blockRegionForPlace(here);
            }
            return regionForHere;
        } else {
            return blockRegionForPlace(p);
        }
    }

    public def containsLocally(p:Point):boolean = get(here).contains(p);

    
    // replicated from superclass to workaround xlC bug with using & itables
    public operator this(p:Place):Region(rank) = get(p);

    public operator this(pt:Point(rank)):Place {
	if (CompilerFlags.checkBounds() && !region.contains(pt)) raiseBoundsError(pt);
        return mapIndexToPlace(pt(axis));
    }

    public operator this(i0:long){rank==1}:Place {
        if (CompilerFlags.checkBounds() && !region.contains(i0)) raiseBoundsError(i0);
        return mapIndexToPlace(i0);
    }

    public operator this(i0:long, i1:long){rank==2}:Place {
        if (CompilerFlags.checkBounds() && !region.contains(i0, i1)) raiseBoundsError(i0,i1);
        switch(axis) {
        case 0: return mapIndexToPlace(i0);
        case 1: return mapIndexToPlace(i1);
        default: return here; // UNREACHABLE
        }
    }

    public operator this(i0:long, i1:long, i2:long){rank==3}:Place {
        if (CompilerFlags.checkBounds() && !region.contains(i0, i1, i2)) raiseBoundsError(i0,i1,i2);
        switch(axis) {
        case 0: return mapIndexToPlace(i0);
        case 1: return mapIndexToPlace(i1);
        case 2: return mapIndexToPlace(i2);
        default: return here; // UNREACHABLE
        }
    }

    public operator this(i0:long, i1:long, i2:long, i3:long){rank==4}:Place {
        if (CompilerFlags.checkBounds() && !region.contains(i0, i1, i2, i3)) raiseBoundsError(i0,i1,i2,i3);
        switch(axis) {
        case 0: return mapIndexToPlace(i0);
        case 1: return mapIndexToPlace(i1);
        case 2: return mapIndexToPlace(i2);
        case 3: return mapIndexToPlace(i3);
        default: return here; // UNREACHABLE
        }
    }

    public def offset(pt:Point(rank)):long {
        val r = get(here);
        val offset = r.indexOf(pt);
        if (offset == -1L) {
            if (CompilerFlags.checkBounds() && !region.contains(pt)) raiseBoundsError(pt);
            if (CompilerFlags.checkPlace()) raisePlaceError(pt);
        }
        return offset;
    }


    public def offset(i0:long){rank==1}:long {
        val r = get(here);
        val offset = r.indexOf(i0);
        if (offset == -1L) {
            if (CompilerFlags.checkBounds() && !region.contains(i0)) raiseBoundsError(i0);
            if (CompilerFlags.checkPlace()) raisePlaceError(i0);
        }
        return offset;
    }

    public def offset(i0:long, i1:long){rank==2}:long {
        val r = get(here);
        val offset = r.indexOf(i0,i1);
        if (offset == -1L) {
            if (CompilerFlags.checkBounds() && !region.contains(i0,i1)) raiseBoundsError(i0,i1);
            if (CompilerFlags.checkPlace()) raisePlaceError(i0,i1);
        }
        return offset;
    }

    public def offset(i0:long, i1:long, i2:long){rank==3}:long {
        val r = get(here);
        val offset = r.indexOf(i0,i1,i2);
        if (offset == -1L) {
            if (CompilerFlags.checkBounds() && !region.contains(i0,i1,i2)) raiseBoundsError(i0,i1,i2);
            if (CompilerFlags.checkPlace()) raisePlaceError(i0,i1,i2);
        }
        return offset;
    }

    public def offset(i0:long, i1:long, i2:long, i3:long){rank==4}:long {
        val r = get(here);
        val offset = r.indexOf(i0,i1,i2,i3);
        if (offset == -1L) {
            if (CompilerFlags.checkBounds() && !region.contains(i0,i1,i2,i3)) raiseBoundsError(i0,i1,i2,i3);
            if (CompilerFlags.checkPlace()) raisePlaceError(i0,i1,i2,i3);
        }
        return offset;
    }

    public def maxOffset() {
        val r = get(here);
        return r.size()-1;
    }
        
    public def restriction(r:Region(rank)):Dist(rank) {
        return new WrappedDistRegionRestricted(this, r);
    }

    public def restriction(p:Place):Dist(rank) {
        return new WrappedDistPlaceRestricted(this, p);
    }

    public def equals(thatObj:Any):boolean {
        if (this == thatObj) return true;
        if (!(thatObj instanceof BlockDist)) return super.equals(thatObj);
        val that = thatObj as BlockDist;
        return this.axis.equals(that.axis) && this.region.equals(that.region);
    }
}

