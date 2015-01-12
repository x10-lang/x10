/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

/**
 * @author bdlucas
 */

final public class SeqMatMultAdd1a extends Benchmark {

    //
    // parameters
    //

    int N = 55*5;
    double operations() {return 1.0*N*N*N;}
    double expected() {return -6866925.0;}
        

    //
    // the benchmark
    //

    final int Na = N;
    final int Nb = N;
    final int Nc = N;

    final double [] a = new double[Na*Na];
    final double [] b = new double[Nb*Nb];
    final double [] c = new double[Nc*Nc];

    {
        for (int i=0; i<Na; i++) {
            for (int j=0; j<Na; j++) {
                a[i*Na+j] = i*j;
                b[i*Na+j] = i-j;
                c[i*Na+j] = i+j;
            }
        }
    }

    double once() {
        for (int i=0; i<Na; i++)
            for (int j=0; j<Nb; j++)
                for (int k=0; k<Nc; k++)
                    a[i*Na+j] += b[i*Nb+k]*c[k*Nc+j];
        return a[10*Na+10];
    }


    //
    // boilerplate
    //

    public static void main(String [] args) {
        new SeqMatMultAdd1a().run();
    }
}
