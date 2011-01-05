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

public class SeqMatMultAdd1a extends Benchmark {

    val N = 55*5;
    def expected() = -6866925.0;
    def operations() = N*N*N as double;

    //
    //
    //

    val r = 0..(N-1)*0..(N-1);
    val a = new Array[double](r, (p:Point)=>p(0)*p(1) as double);
    val b = new Array[double](r, (p:Point)=>p(0)-p(1) as double);
    val c = new Array[double](r, (p:Point)=>p(0)+p(1) as double);

    def once() {
        for (var i:int=0; i<N; i++)
            for (var j:int=0; j<N; j++)
                for (var k:int=0; k<N; k++)
                    a(i,j) += b(i,k)*c(k,j);
        return a(10,10);
    }


    //
    //
    //

    public static def main(Array[String](1)) {
        new SeqMatMultAdd1a().execute();
    }
}
