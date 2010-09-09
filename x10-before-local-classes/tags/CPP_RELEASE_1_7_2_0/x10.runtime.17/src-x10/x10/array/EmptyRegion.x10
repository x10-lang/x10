// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

/**
 * Represents and empty region, implemented as a UnionRegion with no
 * regions.
 *
 * @author bdlucas
 */

value class EmptyRegion extends UnionRegion {

    def this(val rank: int): EmptyRegion{self.rank==rank} {
        super(new PolyRegionListBuilder(rank));
    }

    protected def computeBoundingBox(): Region(rank) {
        throw new IllegalOperationException("bounding box not not defined for empty region");
    }

    public def toString(): String {
        return "empty(" + rank + ")";
    }
}
