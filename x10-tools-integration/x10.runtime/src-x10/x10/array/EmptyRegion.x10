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
