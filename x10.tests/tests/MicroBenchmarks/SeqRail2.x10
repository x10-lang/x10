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
 * Basic rail performance test. Allocate a rail, assign values to each
 * element, read it back.
 *
 * Test is written to simulate treating the rail as a 2-d array in
 * order to facilitate comparisons with array performance tests
 * SeqPseudoArray2*.* and SeqArray2*.*.
 *
 * The following tests all do essentially the same work and so ideally
 * should deliver the same performance: SeqRail2.*,
 * SeqPseudoArray2*.*, SeqArray2*.*.
 *
 * @author bdlucas
 */

public class SeqRail2 extends Benchmark {

    //
    // parameters
    //

    val N = 2000;
    def expected() = 1.0*N*N*(N-1);
    def operations() = 2.0*N*N;


    //
    // the benchmark
    //

    val a = new Rail[double](N*N);

    def once() {
        for (i in 0..(N-1))
            for (j in 0..(N-1))
                a(i*N+j) = (i+j) as double;
        var sum:double = 0.0;
        for (i in 0..(N-1))
            for (j in 0..(N-1))
                sum += a(i*N+j);
        return sum;
    }

    //
    // boilerplate
    //

    public static def main(Rail[String]) {
        new SeqRail2().execute();
    }
}
