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
 * @author vj 03/17/09
 */
public class ArrayAccessEqualRank extends x10Test {

    public def arrayEqual(A: DistArray[int], B: DistArray[int](A.rank)) {
        finish ateach (p in A.dist) {
            val v = at (B.dist(p)) B(p);
            chk(A(p) == v);
        }
    }

    public def run(): boolean = {
    		val D = Dist.make(Region.make(0,9));
    		val a = DistArray.make[Int](D, (Point)=>0n);
    		val b = DistArray.make[Int](D, (Point)=>0n);
    		arrayEqual(a,b);
    		return true;
    }

    public static def main(Rail[String]) = {
        new ArrayAccessEqualRank().execute();
    }
}
