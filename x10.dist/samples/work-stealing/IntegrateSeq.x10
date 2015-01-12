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

/**
 * This is a sequential implementation of the Integrate program (baseline).
 */

import x10.compiler.Ephemeral;

public class IntegrateSeq { 
  static val epsilon = 1.0e-9;

  public static def computeArea(left:double, right:double) {
    return recEval(left, (left*left + 1.0) * left, right, (right*right + 1.0) * right, 0);
  }

  private static def recEval(l:double, fl:double, r:double, fr:double, a:double) {
    @Ephemeral val h = (r - l) / 2;
    @Ephemeral val hh = h / 2;
    val c = l + h;
    val fc = (c*c + 1.0) * c;
    val al = (fl + fc) * hh;
    val ar = (fr + fc) * hh;
    val alr = al + ar;
    if (alr - a < epsilon && a - alr < epsilon) return alr;
    val expr1:double;
    val expr2:double;
    /* finish */ {
      /* async */ { expr1 = recEval(c, fc, r, fr, ar); };
      expr2 = recEval(l, fl, c, fc, al);
    }
    return expr1 + expr2;
  }
 
  public static def main(args:Rail[String]) {
    val xMax = args.size > 0 ? Long.parse(args(0)) : 10;
    var avgDur:Double = 0;
    for (i in 0..9) {
        val v = (i%2 == 0 ? xMax : xMax+1)-i%2;
        val startTime = System.nanoTime();
        val area = computeArea(0, v);
        val duration = ((System.nanoTime() - startTime) as Double)/1e9;
        avgDur += duration;
        Console.OUT.println("The area of (x*x + 1) * x from 0 to "+xMax+" is "+area);
        Console.OUT.printf("Time: %7.3f\n", duration);
    }
    Console.OUT.printf("------------------- Average Time: %7.3f\n", avgDur / 10);
  }
}
