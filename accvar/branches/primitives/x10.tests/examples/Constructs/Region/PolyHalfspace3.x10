/**
 * Create an unbounded region (rectangle missing a side), examine it,
 * observe UnboundedRegionException when attempting to scan it
 */

class PolyHalfspace3 extends TestRegion {

    public def run() {

        val ROW = X(0);
        val COL = X(1);

        val r = 
            reg(2, ROW, GE, 0) &&
            reg(2, ROW, LE, 3) &&
            
            reg(2, COL, LE, 1);
        prUnbounded("unbounded rectangle", r);

        return status();
    }

    def expected() =
        "--- PolyHalfspace3: unbounded rectangle\n"+
        "rank 2\n"+
        "rect true\n"+
        "zeroBased false\n"+
        "rail false\n"+
        "isConvex() true\n"+
        "size() Incomplete method.\n"+
        "region: (x0>=0 && x1<=1 && x0<=3)\n"+
        "x10.array.UnboundedRegionException: axis 1 has no minimum\n";
    
    public static def main(Rail[String]) {
        new PolyHalfspace3().execute();
    }

}
