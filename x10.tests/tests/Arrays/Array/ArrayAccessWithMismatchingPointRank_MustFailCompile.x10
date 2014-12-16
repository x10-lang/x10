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

// We run with STATIC_CHECKS because without it, it will just insert a cast: Warning: Expression 'p' was cast to type ...
//OPTIONS: -STATIC_CHECKS

import harness.x10Test;
import x10.regionarray.*;

/**
 * Simple array test.
 * Testing that ia(p) gives an error, where ia:Array[int](1) and p:Point(2).
 */
public class ArrayAccessWithMismatchingPointRank_MustFailCompile extends x10Test {

    def a(b:int) {}

    public def run(): boolean = {

        val e = Region.make(1,10);
        val ia = new Array[int](e, (Point)=>0); // will infer ia:Array[int](1)
        val p = [1n as Int,1n] as Point; // will infer p:Point(2)

        val p1 = [1n as Int] as Point;
	a(ia(p1)); // ok
        a(ia(p)); // ERR [Method or static constructor not found for given call. Call: ia(Point{self==p, p.Point#rank==2})]

        return true;
    }

    public static def main(Rail[String]) = {
        new ArrayAccessWithMismatchingPointRank_MustFailCompile().execute();
    }
}
