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
 * Simulated array code. This is intended to represent the best
 * possible X10 code for a 2-d generic array class.
 *
 * The following tests all do essentially the same work and so ideally
 * should deliver the same performance: SeqRail2.*,
 * SeqPseudoArray2*.*, SeqArray2*.*.
 *
 * @author bdlucas
 */

public class SeqPseudoArray2b extends Benchmark {

    //
    // parameters
    //

    val N = 2000;
    def expected() = 1.0*N*N*(N-1);
    def operations() = 2.0*N*N;


    //
    // the benchmark
    //

    final static class Arr[T]  {T haszero} implements (int,int)=>T {

        val m0: int;
        val m1: int;
        val raw: Rail[T];
        
        def this(m0:int, m1:int) {
            this.m0 = m0;
            this.m1 = m1;
            this.raw = new Array[T](m0*m1);
        }
        
        final operator this(i0: int, i1: int)=(v:T) {
            raw(i0*m1+i1) = v;
        }
        
        final public operator this(i0:int, i1: int) {
            return raw(i0*m1+i1);
        }
    }
    
    val a = new Arr[double](N, N);

    def once() {
        for (var i:int=0; i<N; i++)
            for (var j:int=0; j<N; j++)
                a(i,j) = (i+j) as double;
        var sum:double = 0.0;
        for (var i:int=0; i<N; i++)
            for (var j:int=0; j<N; j++)
                sum += a(i,j);
        return sum;
    }

    //
    // boilerplate
    //

    public static def main(Array[String](1)) {
        new SeqPseudoArray2b().execute();
    }
}
