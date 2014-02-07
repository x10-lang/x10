/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

/**
 * @author bdlucas
 */

public class SeqRail2 extends Benchmark {

    //
    // parameters
    //

    final int N = 2000;
    double expected() {return 1.0*N*N*(N-1);}
    double operations() {return 2.0*N*N;}


    //
    // the benchmark
    //

    final double [] a = new double[N*N];

    double once() {
        for (int i=0; i<N; i++)
            for (int j=0; j<N; j++)
                a[i*N+j] = (double)(i+j);
        double sum = 0.0;
        for (int i=0; i<N; i++)
            for (int j=0; j<N; j++)
                sum += a[i*N+j];
        return sum;
    }


    //
    // boilerplate
    //

    public static void main(String [] args) {
        new SeqRail2().run();
    }
}


