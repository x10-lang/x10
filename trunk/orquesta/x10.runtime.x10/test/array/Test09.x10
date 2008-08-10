/**
 * Create a diamond missing a side, examine it, observe an
 * UnboundedRegionException when attempting to scan it.
 */

import x10.array.BaseRegion;
import x10.array.ConstraintList;

class Test09 extends TestArray {

    final static int ROW = ConstraintList.X(0);
    final static int COL = ConstraintList.X(1);

    public void run() {
        ConstraintList cl = new ConstraintList(2);
        cl.add(COL+ROW, cl.LE, 7);
        cl.add(COL+ROW, cl.GE, 3);
        cl.add(COL-ROW, cl.LE, 1);
        Region r = BaseRegion.make(cl);
        prUnbounded("unbounded diamond", r);
    }
}
