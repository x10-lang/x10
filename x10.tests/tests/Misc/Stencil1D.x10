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

import harness.x10Test;

/**
 * A simple 1-D stencil example in X10. Uses multiple asyncs in a single place.
 * @author vj 08/15/08
 */
public class Stencil1D extends x10Test {
    static epsilon  = 1E-4D;
    val N: long;
    val P: long;
    var iters: long;
    var delta: double = epsilon+1;

    def this(n: long, p: long) { this.N=n; this.P=p;}

    def step(A:Rail[Double], R:LongRange) {
       var diff: Double = 0;
       for (q in R) {
           val newVal = (A(q-1)+ A(q+1))/2.0 ; 
           diff = Math.max(diff, Math.abs(newVal - A(q)));
           A(q) = newVal;
       }
       return diff;
    }

    public def run() : boolean {
       val A = new Rail[Double](N+2, (long)=>0.0D); 
       A(N+1) = N+1.0D;
       val blocks = block(1..N, P);
       for (; delta > epsilon; iters++) {
          delta = 0;
          finish for (p in 0..(P-1)) async {
             val myDelta  = step(A, blocks(p));
             atomic  delta= Math.max(delta, myDelta);
          }
       }
       return true;
    }

    public static def block(R:LongRange, P:Long):Rail[LongRange] = {
        assert P >=0;
        val low = R.min;
        val high = R.max;
        val count = high-low+1;
        val baseSize = count/P;
        val extra = count - baseSize*P;
        new Rail[LongRange](P, (i:long):LongRange => {
          val start = low+i*baseSize+ (i < extra? i : extra);
          start..(start+baseSize+(i < extra ? 0 : -1))
        })
    }

    public static def main(args: Rail[String]) {
       var n: long = args.size > 0 ? Long.parse(args(0)) : 100;
       var p: long = args.size > 1 ? Long.parse(args(1)) : 2;
       val s = new Stencil1D(n, p); 
       s.execute();
    }
}
