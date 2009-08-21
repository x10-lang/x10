/**
 * Construct a 3-d rectangular region, make an array from from it, and
 * examine it.
 */

class PolyRectangular1 extends TestRegion {

    public def run() {
        var min: Rail[int] = [1, 2, 3];
        var max: Rail[int] = [2, 4, 6];
        var r: Region = Region.makeRectangular(min, max);
        prArray("3-d rect array", r);
        return status();
    }

    def expected() =
        "--- PolyRectangular1: 3-d rect array\n" + 
        "rank 3\n" + 
        "rect true\n" + 
        "zeroBased false\n" + 
        "rail false\n" + 
        "isConvex() true\n" + 
        "size() 24\n" + 
        "region: [1..2,2..4,3..6]\n" + 
        "  poly\n" + 
        "    --- 1\n" + 
        "    2  . . . 6 8 0 2 . . . \n" + 
        "    3  . . . 9 2 5 8 . . . \n" + 
        "    4  . . . 2 6 0 4 . . . \n" + 
        "    --- 2\n" + 
        "    2  . . . 2 6 0 4 . . . \n" + 
        "    3  . . . 8 4 0 6 . . . \n" + 
        "    4  . . . 4 2 0 8 . . . \n" + 
        "  iterator\n" + 
        "    --- 1\n" + 
        "    2  . . . 6 8 0 2 . . . \n" + 
        "    3  . . . 9 2 5 8 . . . \n" + 
        "    4  . . . 2 6 0 4 . . . \n" + 
        "    --- 2\n" + 
        "    2  . . . 2 6 0 4 . . . \n" + 
        "    3  . . . 8 4 0 6 . . . \n" +
        "    4  . . . 4 2 0 8 . . . \n";

    public static def main(Rail[String]) {
        new PolyRectangular1().execute();
    }
}
