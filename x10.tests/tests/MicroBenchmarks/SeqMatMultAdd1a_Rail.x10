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
public class SeqMatMultAdd1a_Rail extends Benchmark {

    var N:Long = 55L*5L;
    def expected() = -6866925.0;
    def operations() = N*N*N as double;

    //
    //
    //

    val a = initA();
    val b = initB();
    val c = initC();

    private def initA() {
        val t = new Rail[double](N*N);
        for (i in 0L..(N-1)) {
            for (j in 0L..(N-1)) {
                t(i*N+j) = i*j;
            }
        }
       return t;
    }

    private def initB() {
        val t = new Rail[double](N*N);
        for (i in 0L..(N-1)) {
            for (j in 0L..(N-1)) {
                t(i*N+j) = i-j;
            }
        }
       return t;
    }

    private def initC() {
        val t = new Rail[double](N*N);
        for (i in 0L..(N-1)) {
            for (j in 0L..(N-1)) {
                t(i*N+j) = i+j;
            }
        }
       return t;
    }

    def once() {
        for (i in 0L..(N-1))
            for (j in 0L..(N-1))
                for (k in 0L..(N-1))
                    a(i*N+j) += b(i*N+k)*c(k*N+j);
        return a(10*N+10);
    }


    //
    //
    //

    public static def main(Rail[String]) {
        new SeqMatMultAdd1a_Rail().execute();
    }
}
