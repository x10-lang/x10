/**
 * Make a full region, examine it, observing an
 * UnboundedRegionException when attemppting to scan it
 */

class PolyFull1 extends TestRegion {

    public def run() {
        var r: Region = Region.makeFull(3);
        prUnbounded("full region", r);
        return status();
    }

    def expected() =
        "--- PolyFull1: full region\n"+
        "rank 3\n"+
        "rect true\n"+
        "zeroBased false\n"+
        "rail false\n"+
        "isConvex() true\n"+
        "size() Incomplete method.\n"+
        "region: full(3)\n"+
        "x10.array.UnboundedRegionException: axis 2 has no minimum\n";
    
    public static def main(Rail[String]) {
        new PolyFull1().execute();
    }
}
