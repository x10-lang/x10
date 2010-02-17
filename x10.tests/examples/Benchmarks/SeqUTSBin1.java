/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

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
