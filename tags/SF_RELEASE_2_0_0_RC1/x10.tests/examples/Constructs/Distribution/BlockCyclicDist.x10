/**
 * Basic distributions
 *
 */

class BlockCyclicDist extends TestDist {

    public def run() {

        val r = [1..4, 1..7] as Region;
        pr("r " + r);

        prDist("blockCyclic 0", Dist.makeBlockCyclic(r, 0, 3));
        prDist("blockCyclic 1", Dist.makeBlockCyclic(r, 1, 3));

        return status();
    }

    def expected() =
        "r [1..4,1..7]\n"+
        "--- blockCyclic 0: Dist(0->[1..3,1..7],1->[4..4,1..7])\n"+
        "    1  . 0 0 0 0 0 0 0 . . \n"+
        "    2  . 0 0 0 0 0 0 0 . . \n"+
        "    3  . 0 0 0 0 0 0 0 . . \n"+
        "    4  . 1 1 1 1 1 1 1 . . \n"+
        "--- blockCyclic 1: Dist(0->[1..4,1..3],1->[1..4,4..6],2->[1..4,7..7])\n"+
        "    1  . 0 0 0 1 1 1 2 . . \n"+
        "    2  . 0 0 0 1 1 1 2 . . \n"+
        "    3  . 0 0 0 1 1 1 2 . . \n"+
        "    4  . 0 0 0 1 1 1 2 . . \n";
    
    public static def main(var args: Rail[String]) {
        new BlockCyclicDist().execute();
    }

}
