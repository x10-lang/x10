/**
 * Create a diamond missing a side, examine it, observe an
 * UnboundedRegionException when attempting to scan it.
 */

class PolyHalfspace2 extends TestRegion {

    public def run() {

        val ROW = X(0);
        val COL = X(1);

        val r =
            reg(2, COL+ROW, LE, 7) &&
            reg(2, COL+ROW, GE, 3) &&
            
            reg(2, COL-ROW, LE, 1);
        prUnbounded("unbounded diamond", r);

        return status();
    }

    def expected() =
        "--- PolyHalfspace2: unbounded diamond\n"+
        "rank 2\n"+
        "rect false\n"+
        "zeroBased false\n"+
        "rail false\n"+
        "isConvex() true\n"+
        "size() Incomplete method.\n"+
        "region: (x0+x1>=3 && x0-x1>=-1 && x0+x1<=7)\n"+
        "x10.array.UnboundedRegionException: axis 0 has no maximum\n";
    
    public static def main(Rail[String]) {
        new PolyHalfspace2().execute();
    }

}
