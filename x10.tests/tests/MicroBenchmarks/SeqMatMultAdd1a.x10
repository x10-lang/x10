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
 * @author bdlucas
 */
public class SeqMatMultAdd1a extends Benchmark {

    var N:long = 55*5;
    def expected() = -6866925.0;
    def operations() = N*N*N as double;

    //
    //
    //

    val a = new Array_2[double](N, N, (i:long,j:long)=>(i*j) as double);
    val b = new Array_2[double](N, N, (i:long,j:long)=>(i-j) as double);
    val c = new Array_2[double](N, N, (i:long,j:long)=>(i+j) as double);

    def once() {
        for (i in 0L..(N-1))
            for (j in 0L..(N-1))
                for (k in 0L..(N-1))
                    a(i,j) += b(i,k)*c(k,j);
        return a(10,10);
    }


    //
    //
    //

    public static def main(Rail[String]) {
        new SeqMatMultAdd1a().execute();
    }
}
