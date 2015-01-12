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

public class SeqRail1b extends Benchmark {

    //
    // parameters
    //

    val N = 1000000;
    val M = 20;
    def expected() = N*M as double;
    def operations() = N*M as double;


    //
    // the benchmark
    //

    val a = new Rail[int](N+M, 1n);

    def once() {
        var sum: int= 0n;
        for (k in 0..(M-1))
            for (i in 0..(N-1))
                sum += a(i+k);
        return sum as double;
    }

    //
    // boilerplate
    //

    public static def main(Rail[String]) {
        new SeqRail1b().execute();
    }
}
