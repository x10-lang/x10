/**
 * Basic distributions
 *
 */

class Test26 extends TestArray {

    public void run() {

        //final region R = [0:N-1,0:N-1];
        //final dist D = dist.factory.block(R);

        final Region r = r(1,4,1,7);
        pr("r " + r);


        prDist("cyclic 0", Dist.makeCyclic(r, 0));
        prDist("cyclic 1", Dist.makeCyclic(r, 1));

        prDist("blockCyclic 0", Dist.makeBlockCyclic(r, 0, 3));
        prDist("blockCyclic 1", Dist.makeBlockCyclic(r, 1, 3));

        prDist("block 0", Dist.makeBlock(r, 0));
        prDist("block 1", Dist.makeBlock(r, 1));

    }
    

}

