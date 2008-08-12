/**
 * Construct a diamond-shaped region from halfspaces, make an array
 * out of it, and examine it.
 */

import x10.array.PolyRegion;
import x10.array.HalfspaceList;

class Test08 extends TestArray {

    final static int ROW = HalfspaceList.X(0);
    final static int COL = HalfspaceList.X(1);

    public void run() {
        HalfspaceList hl = new HalfspaceList(2);
        hl.add(COL+ROW, hl.LE, 7);
        hl.add(COL+ROW, hl.GE, 3);
        hl.add(COL-ROW, hl.LE, 1);
        hl.add(COL-ROW, hl.GE, -1);
        Region r = PolyRegion.make(hl);
        prArray("diamond", r);
    }
}
