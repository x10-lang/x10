// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

/**
 * A full region is the unbounded region that contains all points of
 * its rank, implemented as a PolyRegion with no constraints (no
 * halfspaces).
 *
 * @author bdlucas
 */

class FullRegion extends PolyRegion {

    def this(val rank: int): FullRegion{self.rank==rank} {
        super((new PolyMatBuilder(rank)).toSortedPolyMat(false), true);
    }

    protected global def computeBoundingBox(): Region(rank) {
        return this;
    }

    public global def toString(): String {
        return "full(" + rank + ")";
    }
}
