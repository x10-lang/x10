/**
 * Intersect a rectangular region with a full region, construct an
 * array from it, and examine it.
 */

class PolyIntersection3 extends TestRegion {

    public def run() {
        val r1 = Region.makeRectangular([1,2], [3,4]);
        val r2 = Region.makeFull(2);
        val r3 = r1.intersection(r2);
        prArray("full intersected with rectangle", r3);
        return status();
    }

    def expected() =
        "--- PolyIntersection3: full intersected with rectangle\n"+
        "rank 2\n"+
        "rect true\n"+
        "zeroBased false\n"+
        "rail false\n"+
        "isConvex() true\n"+
        "size() 9\n"+
        "region: [1..3,2..4]\n"+
        "  poly\n"+
        "    1  . . 2 3 4 . . . . . \n"+
        "    2  . . 4 6 8 . . . . . \n"+
        "    3  . . 6 9 2 . . . . . \n"+
        "  iterator\n"+
        "    1  . . 2 3 4 . . . . . \n"+
        "    2  . . 4 6 8 . . . . . \n"+
        "    3  . . 6 9 2 . . . . . \n";
    
    public static def main(Rail[String]) {
        new PolyIntersection3().execute();
    }
}
