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

import x10.array.*;

/**
 * Basic array, X10-style loop
 *
 * @author bdlucas
 */
public class SeqArray2b extends Benchmark {

    //
    // parameters
    //

    val N = 2000;
    def expected() = 1.0*N*N*(N-1);
    def operations() = 2.0*N*N;

    //
    // the benchmark
    //

    val a = new Array_2[double](N, N);

    def once() {
        for (i in 0..(N-1))
            for (j in 0..(N-1))
                a(i,j) = (i+j) as double;
        var sum:double = 0.0;
        for (i in 0..(N-1))
            for (j in 0..(N-1))
            sum += a(i,j);
        return sum;
    }

    //
    // boilerplate
    //

    public static def main(Rail[String]) {
        new SeqArray2b().execute();
    }
}
