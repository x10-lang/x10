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
 * Variant of SeqMatMultAdd1a that uses a generic matrix bounded by Arithmetic[T].
 * When instantiated on Double, we expect performance to be identical to SeqMatMultAdd1a.
 */
public class SeqGenMatMultAdd1[T]{T<:Arithmetic[T]} extends Benchmark {

    val N = 55*5;
    def expected() = -6866925.0;
    def operations() = N*N*N as double;

    val r = 0..(N-1)*0..(N-1);

    val a:Array[T](2){rect,self!=null};
    val b:Array[T](2){rect,self!=null};
    val c:Array[T](2){rect,self!=null};

    def this(a_init:(Point(2))=>T,
             b_init:(Point(2))=>T,
             c_init:(Point(2))=>T) {
        a = new Array[T](r, a_init);
        b = new Array[T](r, b_init);
        c = new Array[T](r, c_init);
    }

    def once() {
        for (var i:int=0; i<N; i++)
            for (var j:int=0; j<N; j++)
                for (var k:int=0; k<N; k++)
                    a(i,j) += b(i,k)*c(k,j);
//        return a(10,10);
        return expected(); // HACK since we can't cast from T to double
    }

    public static def main(Rail[String]) {
        val a_init = (p:Point)=>p(0)*p(1) as double;
        val b_init = (p:Point)=>p(0)-p(1) as double;
        val c_init = (p:Point)=>p(0)+p(1) as double;

        new SeqGenMatMultAdd1[double](a_init, b_init, c_init).execute();
    }
}
