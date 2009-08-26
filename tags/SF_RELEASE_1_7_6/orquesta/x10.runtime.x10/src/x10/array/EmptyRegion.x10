package x10.array;

//
// Implemented as a UnionRegion with no regions.
//

class EmptyRegion extends UnionRegion {

    EmptyRegion(:self.rank==rank)(final int rank) {
        super(new PolyRegionList(rank));
    }

    public Region boundingBox() {
        return this;
    }

    public String toString() {
        return "empty(" + rank + ")";
    }
}
