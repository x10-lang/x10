// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

/**
 * Implemented as a UnionRegion with no regions.
 */

value class EmptyRegion extends UnionRegion {

    def this(val rank: int): EmptyRegion{self.rank==rank} {
        super(new PolyRegionList(rank));
    }

    public def boundingBox(): Region(rank) {
        throw new IllegalOperationException("bounding box not not defined for empty region");
    }

    public def toString(): String {
        return "empty(" + rank + ")";
    }
}
