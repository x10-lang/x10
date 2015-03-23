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
import x10.regionarray.*;

/**
 * Test that a can be accessed through point p if p ranges over b.dist
 and a.rank has been declared to be b.rank.

 * @author vj 03/17/09 -- fails compilation.
 * What is highly annoying is that ateach (x(i):Point(D.rank) in D) b(x)=i
 * succeeds. (See ArrayAccessEqualRank3.x10.) 
 * The compiler cant figure out that when iterating over D
 * the points have rank D.rank??

 */

public class ArrayAccessEqualRank3a extends x10Test {

    public def run(): boolean {
        val R = Region.make(0,9);
	val b = new Array[Int](R,(Point)=>0n);
	finish for (x[i] in R) async b(x)=(i as int);
        return true;
    }

    public static def main(Rail[String]) {
        new ArrayAccessEqualRank3a().execute();
    }
}
