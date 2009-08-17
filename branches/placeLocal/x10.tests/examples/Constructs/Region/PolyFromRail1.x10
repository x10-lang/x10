/**
 * Construct an array from a native rail and examine it.
 */

class PolyFromRail1 extends TestRegion {

    public def run() {
        var foo: Rail[double] = [5.0,4.0,3.0,2.0,1.0];
        var a: Array[double] = Array.make[double](foo);
        prArray("from native rail", a);
        return status();
    }

    def expected() =
        "--- PolyFromRail1: from native rail\n"+
        "rank 1\n"+
        "rect true\n"+
        "zeroBased true\n"+
        "rail true\n"+
        "isConvex() true\n"+
        "size() 5\n"+
        "region: [0..4]\n"+
        "  poly\n"+
        "5 4 3 2 1 . . . . . \n"+
        "  iterator\n"+
        "5 4 3 2 1 . . . . . \n";
    
    public static def main(Rail[String]) {
        new PolyFromRail1().execute();
    }
}
