/**
 * Construct a diamond-shaped region from constraints, make an array
 * out of it, and examine it.
 */

import x10.array.BaseRegion;
import x10.array.ConstraintList;

class Test08 extends TestArray {

    final static int ROW = ConstraintList.X(0);
    final static int COL = ConstraintList.X(1);

    public void run() {
        ConstraintList cl = new ConstraintList(2);
        cl.add(COL+ROW, cl.LE, 7);
        cl.add(COL+ROW, cl.GE, 3);
        cl.add(COL-ROW, cl.LE, 1);
        cl.add(COL-ROW, cl.GE, -1);
        Region r = BaseRegion.make(cl);
        prArray("diamond", r);
    }
}
