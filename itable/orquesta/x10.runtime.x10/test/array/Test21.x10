/**
 * Difference for some non-rectangular regions.
 *
 * Uses "bump" argument to prArray to probe for whether constituent
 * regions of UnionRegion are disjoint: all array elements in region
 * should be bumped once and therefore all should be 1.
 */

import x10.array.HalfspaceList;
import x10.array.PolyRegion;
import x10.array.PolyRegion;


class Test21 extends TestArray {

    public void run() {

        Region r1 = Region.makeRectangular(new int [] {0,0}, new int [] {5,5});
        prArray("r1", r1, true);

        int ROW = HalfspaceList.X(0);
        int COL = HalfspaceList.X(1);
        HalfspaceList hl = new HalfspaceList(2);
        hl.add(COL+ROW, hl.LE, 7);
        hl.add(COL+ROW, hl.GE, 3);
        hl.add(COL-ROW, hl.LE, 1);
        hl.add(COL-ROW, hl.GE, -1);
        Region r2 = PolyRegion.make(hl);
        prArray("r2", r2, true);

        Region r3 = PolyRegion.makeUpperTriangular(1, 1, 4);
        prArray("r3", r3, true);

        prArray("r1.difference(r1)", r1.difference(r1), true);
        prArray("r1.difference(r2)", r1.difference(r2), true);
        prArray("r1.difference(r3)", r1.difference(r3), true);

        prArray("r2.difference(r1)", r2.difference(r1), true);
        prArray("r2.difference(r2)", r2.difference(r2), true);
        prArray("r2.difference(r3)", r2.difference(r3), true);

        prArray("r3.difference(r1)", r3.difference(r1), true);
        prArray("r3.difference(r2)", r3.difference(r2), true);
        prArray("r3.difference(r3)", r3.difference(r3), true);

    }

}


