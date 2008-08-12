package x10.array;

//
// Implemented as a UnionRegion with no regions.
//

class EmptyRegion extends UnionRegion {

    EmptyRegion(int rank) {
        super(new PolyRegionList(rank));
    }

    public String toString() {
        return "empty(" + rank + ")";
    }
}
