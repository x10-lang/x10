/**
 * Basic distributions
 *
 */

class CyclicDist extends TestDist {

    public def run() {

        val r = [1..4, 1..7] as Region;
        pr("r " + r);

        prDist("cyclic 0", Dist.makeCyclic(r, 0));
        prDist("cyclic 1", Dist.makeCyclic(r, 1));

        return status();
    }

    def expected() =
        "r [1..4,1..7]\n"+
        "--- cyclic 0: Dist(0->[1..1,1..7],1->[2..2,1..7],2->[3..3,1..7],3->[4..4,1..7])\n"+
        "    1  . 0 0 0 0 0 0 0 . . \n"+
        "    2  . 1 1 1 1 1 1 1 . . \n"+
        "    3  . 2 2 2 2 2 2 2 . . \n"+
        "    4  . 3 3 3 3 3 3 3 . . \n"+
        "--- cyclic 1: Dist(0->([1..4,1..1] || [1..4,5..5]),1->([1..4,2..2] || [1..4,6..6]),2->([1..4,3..3] || [1..4,7..7]),3->[1..4,4..4])\n"+
        "    1  . 0 1 2 3 0 1 2 . . \n"+
        "    2  . 0 1 2 3 0 1 2 . . \n"+
        "    3  . 0 1 2 3 0 1 2 . . \n"+
        "    4  . 0 1 2 3 0 1 2 . . \n";
    
    public static def main(var args: Rail[String]) {
        new CyclicDist().execute();
    }

}
