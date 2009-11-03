/**
 * Constant distribution
 *
 * (Was ConstDist)
 */

class PolyConst1 extends TestDist {

    public def run() {
	
	var r: Region = r(0,9,0,9);
        pr("r " + r);

	var d: Dist = Dist.makeConstant(r, here);
        pr("d " + d);

        var a: Array[double](r) = Array.make[double](Dist.makeConstant(r, here));
        pr("a " + a);

        var b: Array[double](r) = Array.make[double](Dist.makeConstant(r, here));
        pr("b " + b);
		


        return status();
    }

    def expected() =
        "r [0..9,0..9]\n"+
        "d Dist(0->[0..9,0..9])\n"+
        "a Array(Dist(0->[0..9,0..9]))\n"+
        "b Array(Dist(0->[0..9,0..9]))\n";
    
    public static def main(Rail[String]) {
        new PolyConst1().execute();
    }
}
