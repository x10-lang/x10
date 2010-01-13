// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

/**
 * Represents and empty region, implemented as a UnionRegion with no
 * regions.
 *
 * @author bdlucas
 */

class EmptyRegion extends UnionRegion {

    def this(val rank: int): EmptyRegion{self.rank==rank} {
        super(new PolyRegionListBuilder(rank));
    }

    public global def product(r: Region): Region(rank) {
        return this;
    }

    protected global def computeBoundingBox(): Region(rank) {
        throw U.illegal("bounding box not not defined for empty region");
    }

    public global safe def toString() = "empty(" + rank + ")";
}
