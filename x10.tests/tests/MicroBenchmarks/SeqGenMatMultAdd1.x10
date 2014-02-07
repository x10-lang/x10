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

import x10.array.*;

/**
 * Variant of SeqMatMultAdd1a that uses a generic matrix bounded by Arithmetic[T].
 * When instantiated on Double, we expect performance to be identical to SeqMatMultAdd1a.
 */
public class SeqGenMatMultAdd1[T]{T<:Arithmetic[T]} extends Benchmark {

    val N:Long = 55*5;
    def expected() = -6866925.0;
    def operations() = N*N*N as double;

    val a:Array_2[T];
    val b:Array_2[T];
    val c:Array_2[T];

    def this(a_init:(long,long)=>T,
             b_init:(long,long)=>T,
             c_init:(long,long)=>T) {
        a = new Array_2[T](N, N, a_init);
        b = new Array_2[T](N, N, b_init);
        c = new Array_2[T](N, N, c_init);
    }

    def once() {
        for (i in 0L..(N-1))
            for (j in 0L..(N-1))
                for (k in 0L..(N-1))
                    a(i,j) += b(i,k)*c(k,j);
//        return a(10,10);
        return expected(); // HACK since we can't cast from T to double
    }

    public static def main(Rail[String]) {
        val a_init = (i:long,j:long)=>(i*j) as double;
        val b_init = (i:long,j:long)=>(i-j) as double;
        val c_init = (i:long,j:long)=>(i+j) as double;

        new SeqGenMatMultAdd1[double](a_init, b_init, c_init).execute();
    }
}
