// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test.

import java.util.Random;

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

    long next(long r, int i) {
        long seed = r+i;
        seed = (seed ^ 0x5DEECE66DL) & ((1L << 48) - 1);
        for (int k=0; k<11; k++)
            seed = (seed * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);
        int l0 = (int) (seed >>> (48 - 32));
        seed = (seed * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);
        int l1 = (int) (seed >>> (48 - 32));
        return (((long)l0) << 32) + l1;
    }

    final double scale = ((double)Long.MAX_VALUE) - ((double)Long.MIN_VALUE);
    double number(long r) {return (r / scale) - (Long.MIN_VALUE / scale);}

    int size = 0;
    int sumb = 0;

    void visit(long r) {
        int b = number(r)<q? m : 0;
        sumb += b;
        size++;
        for (int i=0; i<b; i++)
            visit(next(r,i));
    }
    
    boolean first = true;

    double once() {

        // root node
        size = 0;
        sumb = 0;
        for (int i=0; i<b0; i++)
            visit(next(r0,i));

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
