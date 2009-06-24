package x10.array;

//
// Implemented as a PolyRegion with no halfspaces.
//

class FullRegion extends PolyRegion {

    FullRegion(:self.rank==rank)(final int rank) {
        super(new HalfspaceList(rank));
    }

    public Region boundingBox() {
        return this;
    }

    public String toString() {
        return "full(" + rank + ")";
    }
}
