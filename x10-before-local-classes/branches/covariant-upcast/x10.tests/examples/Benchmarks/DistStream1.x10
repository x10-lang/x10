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
 * Version of Stream with a collection of local arrays implementing a
 * global array.
 */

public class DistStream1 extends Benchmark {

    const alpha = 1.5;
    const beta = 2.5;
    const gamma = 3.0;

    const NUM_TIMES = 10;
    const PARALLELISM = 2;
    const localSize = 512*1024;

    public def operations() = 1.0 * localSize * PARALLELISM * NUM_TIMES;
    public def expected() = (localSize+1)*(alpha+gamma*beta);

    //
    //
    //

    val as = ValRail.make[Rail[double]](PARALLELISM, (p:int) =>
        at (Place.place(p)) Rail.make[double](localSize)
    );
    
    val bs = ValRail.make[ValRail[double]](PARALLELISM, (p:int) => 
        at (Place.place(p)) ValRail.make[double](localSize, (i:int)=>alpha*(p*localSize+i))
    );

    val cs = ValRail.make[ValRail[double]](PARALLELISM, (p:int)=>
        at (Place.place(p)) ValRail.make[double](localSize, (i:int)=>beta*(p*localSize+i))
    );

    public def once() {
        finish for (var p:int=0; p<PARALLELISM; p++) {
            val pl = Place.place(p);
            val a = as(p) as Rail[double]!pl;
            val b = bs(p);
            val c = cs(p);
            async (pl) {
                for (var tt:int=0; tt<NUM_TIMES; tt++) // XTENLANG-311
                    for (var i:int=0; i<localSize; i++)
                        a(i) = b(i) + gamma*c(i);
            }
        }
        val p1 = Place.place(1);
        val a = as(1) as Rail[double]!p1;
        return at (p1) a(1);
    }

    //
    //
    //

    public static def main(Rail[String]) {
        new DistStream1().execute();
    }

}
