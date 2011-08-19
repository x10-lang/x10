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

public class SeqArray1 extends Benchmark {

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

    val a = new Array[double](0..(N+M-1), (Point(1))=>1.0);

    def once() {
        var sum: double = 0.0;
        for (var k:int=0; k<M; k++)
            for (var i:int=0; i<N; i++)
                sum += a(i+k);
        return sum;
    }

    //
    // boilerplate
    //

    public static def main(Array[String](1)) {
        new SeqArray1().execute();
    }
}
