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

final public class SeqMatMultAdd1a_long extends Benchmark {

    //
    // parameters
    //

    long N = 55*5;
    double operations() {return 1.0*N*N*N;}
    double expected() {return -6866925.0;}
        

    //
    // the benchmark
    //

    final long Na = N;
    final long Nb = N;
    final long Nc = N;

    final double [] a = new double[(int)(Na*Na)];
    final double [] b = new double[(int)(Nb*Nb)];
    final double [] c = new double[(int)(Nc*Nc)];

    {
        for (long i=0; i<Na; i++) {
            for (long j=0; j<Na; j++) {
                a[(int)(i*Na+j)] = i*j;
                b[(int)(i*Na+j)] = i-j;
                c[(int)(i*Na+j)] = i+j;
            }
        }
    }

    double once() {
        for (long i=0; i<Na; i++)
            for (long j=0; j<Nb; j++)
                for (long k=0; k<Nc; k++)
                    a[(int)(i*Na+j)] += b[(int)(i*Nb+k)]*c[(int)(k*Nc+j)];
        return a[(int)(10*Na+10)];
    }


    //
    // boilerplate
    //

    public static void main(String [] args) {
        new SeqMatMultAdd1a_long().run();
    }
}
