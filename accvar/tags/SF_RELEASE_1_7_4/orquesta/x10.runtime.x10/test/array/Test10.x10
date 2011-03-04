/**
 * Create an unbounded region (rectangle missing a side), examine it,
 * observe UnboundedRegionException when attempting to scan it
 */

import x10.array.PolyRegion;
import x10.array.HalfspaceList;

class Test10 extends TestArray {

    final static int ROW = HalfspaceList.X(0);
    final static int COL = HalfspaceList.X(1);

    public void run() {
        HalfspaceList hl = new HalfspaceList(2);
        hl.add(ROW, hl.GE, 0);
        hl.add(ROW, hl.LE, 3);
        hl.add(COL, hl.LE, 1);
        Region r = PolyRegion.make(hl);
        prUnbounded("unbounded rectangle", r);
    }
}
