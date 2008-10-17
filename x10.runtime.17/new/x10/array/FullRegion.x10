// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

/*
 * Implemented as a PolyRegion with no halfspaces.
 */

value class FullRegion extends PolyRegion {

    def this(val rank: int): FullRegion{self.rank==rank} {
        super(new HalfspaceList(rank));
    }

    public def boundingBox(): Region(rank) {
        return this;
    }

    public def toString(): String {
        return "full(" + rank + ")";
    }
}
