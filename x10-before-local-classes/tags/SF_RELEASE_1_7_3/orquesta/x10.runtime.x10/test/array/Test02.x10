/**
 * Construct an upper triangular region, make an array from it, and
 * examine it.
 */

class Test02 extends TestArray {

    public void run() {
        Region r = Region.makeUpperTriangular(5);
        prArray("makeUpperTriangular(5)", r);
    }
}

