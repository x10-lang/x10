package x10.array;

//
// Implemented as a PolyRegion with no halfspaces.
//

class FullRegion extends PolyRegion {

    FullRegion(int rank) {
        super(new HalfspaceList(rank));
    }

    public String toString() {
        return "full(" + rank + ")";
    }
}
