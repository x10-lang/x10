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
 * A simple 1-D stencil example in X10. Uses multiple asyncs in a single place.
 * @author vj 08/15/08
 * //done
 */
public class StencilOrig {
    const epsilon  = 1E-1D;
    val N: int, P: int;
    var iters: int;
    var delta: double = epsilon+1;

    def this(n: int, p: int) { this.N=n; this.P=p;}

    def step(A:Rail[Double]!, R: Region(1), c: Clock) {
       var diff: Double = 0;
       for ((q) in R) {
           val newVal = (A(q-1)+ A(q+1))/2.0 ; 
           diff = Math.max(diff, Math.abs(newVal - A(q)));
           next;
           A(q) = newVal;
           next;
       }
       return diff;
    }

    public def run() : boolean {
       val A = Rail.make[Double](N+2, (int)=>0.0D); 
       A(N+1) = N+1.0D;
       val blocks = block(1..N, P);
       val c = Clock.make();
       for (; delta > epsilon; iters++) {
          delta = 0;
          for ((p):Point(1) in 1..P-1) async clocked(c){
             val myDelta  = step(A, blocks(p), c);
             atomic  delta= Math.max(delta, myDelta);
          }
          val myDelta  = step(A, blocks(0), c);
          atomic  delta= Math.max(delta, myDelta);
          next;
       }
       return true;
    }

    public static def block(R: Region(1), P:Int):ValRail[Region(1)](P) = {
        assert P >=0;
        val low = R.min()(0), high = R.max()(0), count = high-low+1;
        val baseSize = count/P, extra = count - baseSize*P;
        ValRail.make[Region(1)](P, (i:int):Region(1) => {
          val start = low+i*baseSize+ (i < extra? i:extra);
          start..start+baseSize+(i < extra?0:-1)
          })
    }

    public static def main(args: Rail[String]!) {
       var n: int = args.length > 0 ? Int.parse(args(0)) : 100;
       var p: int = args.length > 1 ? Int.parse(args(1)) : 2;
       // x10.io.Console.OUT.println("Starting: N=" + n + " P=" + p);
        var time: Long = -System.nanoTime();
       val s = new StencilOrig(n, p); s.run();
        time += System.nanoTime();
       x10.io.Console.OUT.println("N=" + n + " P=" + p + " Iters=" + s.iters + " time=" + time/(1000*1000) + " ms");
    }
}
