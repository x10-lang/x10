/**
 * Create a diamond missing a side, examine it, observe an
 * UnboundedRegionException when attempting to scan it.
 */

import x10.array.PolyRegion;
import x10.array.HalfspaceList;

class Test09 extends TestArray {

    final static int ROW = HalfspaceList.X(0);
    final static int COL = HalfspaceList.X(1);

    public void run() {
        HalfspaceList hl = new HalfspaceList(2);
        hl.add(COL+ROW, hl.LE, 7);
        hl.add(COL+ROW, hl.GE, 3);
        hl.add(COL-ROW, hl.LE, 1);
        Region r = PolyRegion.make(hl);
        prUnbounded("unbounded diamond", r);
    }
}
