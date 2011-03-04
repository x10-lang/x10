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
 * This class is an optimized implementation for a
 * Block distribution that maps points in its region
 * in a blocked fashion to all the places (the world).<p>
 *
 * Since it includes all Places, it doesn't need to maintain an 
 * explicit Place set of the places it is defined over.
 * Since it is a simple Block distribution, it doesn't
 * need very much information to compute the Region that is mapped 
 * to a given place from the overall Region.<p>
 * 
 * It caches the region for the current place as a transient field.
 * This makes the initial access to this information somewhat slow,
 * but optimizes the wire-transfer size of the Dist object. 
 * This appears to be the appropriate tradeoff, since Dist objects
 * are frequently serialized and usually the restriction operation is 
 * applied to get the Region for here, not for other places.
 */
final class BlockWorldDist extends Dist {
   
    /**
     * The axis along which the region is being distributed
     */
    private val axis:int;

    /**
     * Cached restricted region for the current place.
     */
    private transient var regionForHere:Region(this.rank);


    def this(r:Region, axis:int):BlockWorldDist{this.region==r} {
        super(r, false /* TODO: don't just assume this, check */, Place.MAX_PLACES==1, Place.place(0));
	this.axis = axis;
    }


    /**
     * The key algorithm for this class.
     * Compute the region for the given place by doing region algebra.
     * TODO: Create an optimized fast-path for RectRegion.
     */
    private def blockRegionForPlace(place:Place):Region{self.rank==this.rank} {
        val b = region.boundingBox();
        val min = b.min(axis);
        val max = b.max(axis);
        val P = Place.MAX_PLACES;
        val numElems = max - min + 1;
        val blockSize = numElems/P;
        val leftOver = numElems - P*blockSize;
        val i = place.id;

        val r1 = Region.makeFull(axis);
        val low = min + blockSize*i + (i< leftOver ? i : leftOver);
        val hi = low + blockSize + (i < leftOver ? 0 : -1);
        val r2 = low..hi;
        val r3 = Region.makeFull(region.rank-axis-1);

        return (r1.product(r2).product(r3) as Region(region.rank)).intersection(region);
    }

    /**
     * Given an index into the "axis dimension" determine which place it 
     * is mapped to.
     * Assumption: Caller has done error checking to ensure that index is 
     *   actually within the bounds of the axis dimension.
     */
    private def mapIndexToPlace(index:int) {
        val bb = region.boundingBox();
	val min = bb.min(axis);
        val max = bb.max(axis);
	val P = Place.MAX_PLACES;
        val numElems = max - min + 1;
        val blockSize = numElems/P;
        val leftOver = numElems - P*blockSize;
	val normalizedIndex = index-min;

	val nominalPlace = normalizedIndex/(blockSize+1);
	if (nominalPlace < leftOver) {
	    return Place(nominalPlace);
        } else {
	    val indexFromTop = max-index;
	    return Place(Place.MAX_PLACES - 1 - (indexFromTop/(blockSize)));
        }
    }


    public def places():Sequence[Place]=Place.places();

    public def numPlaces():int = Place.MAX_PLACES;

    public def regions():Sequence[Region(rank)] {
	return new Array[Region(rank)](Place.MAX_PLACES, (i:int)=>blockRegionForPlace(Place.place(i))).sequence();
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

    public def apply(pt:Point(rank)):Place {
	if (CompilerFlags.checkBounds() && !region.contains(pt)) raiseBoundsError(pt);
        return mapIndexToPlace(pt(axis));
    }

    public def apply(i0:int){rank==1}:Place {
	if (CompilerFlags.checkBounds() && !region.contains(i0)) raiseBoundsError(i0);
	return mapIndexToPlace(i0);
    }

    public def apply(i0:int, i1:int){rank==2}:Place {
	if (CompilerFlags.checkBounds() && !region.contains(i0, i1)) raiseBoundsError(i0,i1);
	switch(axis) {
	    case 0: return mapIndexToPlace(i0);
	    case 1: return mapIndexToPlace(i1);
	    default: return here; // UNREACHABLE
        }
    }

    public def apply(i0:int, i1:int, i2:int){rank==3}:Place {
	if (CompilerFlags.checkBounds() && !region.contains(i0, i1, i2)) raiseBoundsError(i0,i1,i2);
	switch(axis) {
	    case 0: return mapIndexToPlace(i0);
	    case 1: return mapIndexToPlace(i1);
	    case 2: return mapIndexToPlace(i2);
	    default: return here; // UNREACHABLE
        }
    }

    public def apply(i0:int, i1:int, i2:int, i3:int){rank==4}:Place {
	if (CompilerFlags.checkBounds() && !region.contains(i0, i1, i2, i3)) raiseBoundsError(i0,i1,i2,i3);
	switch(axis) {
	    case 0: return mapIndexToPlace(i0);
	    case 1: return mapIndexToPlace(i1);
	    case 2: return mapIndexToPlace(i2);
	    case 3: return mapIndexToPlace(i3);
	    default: return here; // UNREACHABLE
        }
    }

    public def restriction(r:Region(rank)):Dist(rank) {
	return new WrappedDistRegionRestricted(this, r) as Dist(rank); // TODO: cast should not be needed
    }

    public def restriction(p:Place):Dist(rank) {
	return new WrappedDistPlaceRestricted(this, p) as Dist(rank); // TODO: cast should not be needed
    }


    public def equals(thatObj:Any):boolean {
	if (!(thatObj instanceof BlockWorldDist)) return false;
        val that = thatObj as BlockWorldDist;
	return this.axis.equals(that.axis) && this.region.equals(that.region);
    }
}

