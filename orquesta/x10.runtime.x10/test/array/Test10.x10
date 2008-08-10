/**
 * Create an unbounded region (rectangle missing a side), examine it,
 * observe UnboundedRegionException when attempting to scan it
 */

import x10.array.BaseRegion;
import x10.array.ConstraintList;

class Test10 extends TestArray {

    final static int ROW = ConstraintList.X(0);
    final static int COL = ConstraintList.X(1);

    public void run() {
        ConstraintList cl = new ConstraintList(2);
        cl.add(ROW, cl.GE, 0);
        cl.add(ROW, cl.LE, 3);
        cl.add(COL, cl.LE, 1);
        Region r = BaseRegion.make(cl);
        prUnbounded("unbounded rectangle", r);
    }
}
