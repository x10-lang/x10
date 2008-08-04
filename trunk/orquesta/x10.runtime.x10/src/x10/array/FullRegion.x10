package x10.array;

import x10.lang.Region;
import x10.lang.Point;

//
// Implemented as a PolyRegion with no constraints.
//
// XXX probably don't need separate class - just make in PolyRegion.makeFull
//

class FullRegion extends PolyRegion {

    FullRegion(int rank) {
        super(new ConstraintList(rank));
    }
}
