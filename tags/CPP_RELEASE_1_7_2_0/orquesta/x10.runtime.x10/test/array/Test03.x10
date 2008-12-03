/**
 * Intersect a rectangular region with a triangular region, construct
 * an array from it, and examine it.
 */

import x10.array.PolyRegion;
import x10.array.HalfspaceList;

class Test03 extends TestArray {

    public void run() {

        Region r1 = PolyRegion.makeUpperTriangular(2, 1, 7);
        prArray("tri", r1);

        int [] min = {3, 3};
        int [] max = {6, 7};
        Region r2 = Region.makeRectangular(min, max);
        prArray("rect", r2);

        Region r3 = r2.intersection(r1);
        prArray("intersection", r3);
    }
}

