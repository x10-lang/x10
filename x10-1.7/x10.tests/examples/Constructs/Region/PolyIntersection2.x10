/**
 * Take an intersection of two rectangular regions, construct an array
 * from it, and examine it.
 */

class PolyIntersection2 extends TestRegion {

    public def run() {
        val r1 = Region.makeRectangular([1,2], [5,7]);
        val r2 = Region.makeRectangular([3,3], [8,9]);
        val r3 = r1.intersection(r2);
        prArray("rectangular intersection", r3);
        return status();
    }

    def expected() =
        "--- PolyIntersection2: rectangular intersection\n"+
        "rank 2\n"+
        "rect true\n"+
        "zeroBased false\n"+
        "rail false\n"+
        "isConvex() true\n"+
        "size() 15\n"+
        "region: [3..5,3..7]\n"+
        "  poly\n"+
        "    3  . . . 9 2 5 8 1 . . \n"+
        "    4  . . . 2 6 0 4 8 . . \n"+
        "    5  . . . 5 0 5 0 5 . . \n"+
        "  iterator\n"+
        "    3  . . . 9 2 5 8 1 . . \n"+
        "    4  . . . 2 6 0 4 8 . . \n"+
        "    5  . . . 5 0 5 0 5 . . \n";
    
    public static def main(Rail[String]) {
        new PolyIntersection2().execute();
    }
}
