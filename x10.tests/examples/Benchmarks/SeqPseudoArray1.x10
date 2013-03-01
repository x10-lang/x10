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

public class SeqPseudoArray1 extends Benchmark {

    //
    // parameters
    //

    val N = 2000;
    def expected() = 1.0;
    def operations() = N*N as double;


    //
    // the benchmark
    //

    final static class Arr {

        val m0: int;
        val m1: int;
        val raw: Rail[double];
        
        def this(m0:int, m1:int) {
            this.m0 = m0;
            this.m1 = m1;
            this.raw = new Rail[double](m0*m1);
        }
        
        final operator this(i0: int, i1: int)=(v:double) {
            raw(i0*m1+i1) = v;
        }
        
        final public operator this(i0:int, i1: int) {
            return raw(i0*m1+i1);
        }
    }
    
    val a = new Arr(N, N);

    def once() {
        for (var i:int=0; i<N; i++)
            for (var j:int=0; j<N; j++)
                a(i,j) += 1;
        return a(20,20);
    }

    //
    // boilerplate
    //

    public static def main(Rail[String]) {
        new SeqPseudoArray1().execute();
    }
}
