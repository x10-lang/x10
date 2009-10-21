// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test.

/**
 * @author bdlucas
 */

public class ArrayLift extends TestArray {

    public const N: int = 9;

    public def run(): boolean {

        val dist = Dist.makeBlockCyclic(0..N, 0, 2);
        prDist("dist", dist);

        pr("--- original");
        val a: Array[double](dist) = Array.make[double](dist, (p:Point)=>p(0) as double);
        for (pt:Point(1) in a) {
            val x = (future(a.dist(pt)) a(pt)).force();
            out.print(x + " ");
        }
        out.println();

        pr("--- lifted");
        val b = a.lift((a:double)=>1.5*a) as Array[double](dist);
        for (pt:Point(1) in b) {
            val x = (future(b.dist(pt)) b(pt)).force();
            out.print(x + " ");
        }
        out.println();

        return status();
    }

    public static def main(var args: Rail[String]): void = {
        new ArrayLift().execute();
    }

    def expected() = 
        "--- dist: Dist(0->([0..1] || [8..9]),1->[2..3],2->[4..5],3->[6..7])\n" + 
        "0 0 1 1 2 2 3 3 0 0 \n" +
        "--- original\n" +
        "0.0 1.0 2.0 3.0 4.0 5.0 6.0 7.0 8.0 9.0 \n" +
        "--- lifted\n" +
        "0.0 1.5 3.0 4.5 6.0 7.5 9.0 10.5 12.0 13.5 \n";

}
