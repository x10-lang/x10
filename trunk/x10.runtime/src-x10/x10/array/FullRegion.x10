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

    protected global def computeBoundingBox(): Region(rank){self.rect} {
        return this;
    }

    public global safe def toString(): String {
        return "full(" + rank + ")";
    }
}
