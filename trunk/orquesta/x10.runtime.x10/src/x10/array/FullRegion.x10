package x10.array;

import x10.lang.Region;
import x10.lang.Point;

//
// XXX not completely implemented - implement as PolyRegion w/ no constraints
//

class FullRegion extends BaseRegion {

    FullRegion(int rank) {
        super(rank, true, false);
    }
}
