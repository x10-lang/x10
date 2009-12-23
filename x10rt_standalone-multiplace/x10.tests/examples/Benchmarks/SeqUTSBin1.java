// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test.

/**
 * @author bdlucas
 */

class SeqUTSBin1 extends Benchmark {

    //
    // parameters
    //

    final int r0 = 0;

    final int b0 = 50000;
    final double q = 0.12;
    final int m = 8;
    double expected() {return 1234872.0;}

    double operations() {return size;}


    //
    // the benchmark
    //

    int size = 0;
    int sumb = 0;

    void visit(long r) {
        int b = UTSRand.number(r)<q? m : 0;
        sumb += b;
        size++;
        for (int i=0; i<b; i++)
            visit(UTSRand.next(r,i));
    }
    
    boolean first = true;

    double once() {

        // root node
        size = 0;
        sumb = 0;
        for (int i=0; i<b0; i++)
            visit(UTSRand.next(r0,i));

        // sanity check
        if (first) {
            double expSize = b0 / (1.0 - q*m);
            double obsBranch = (double)sumb / size;
            double expBranch = q * m;
            System.out.printf("exp size / obs size: %.3f\n", expSize/size);
            System.out.printf("exp branching / obs branching: %.3f\n", expBranch / obsBranch);
        } 
        first = false;

        // should always get same size tree
        return size;
    }


    //
    // boilerplate
    //


    public static void main(String [] args) {
        new SeqUTSBin1().run();
    }
}
