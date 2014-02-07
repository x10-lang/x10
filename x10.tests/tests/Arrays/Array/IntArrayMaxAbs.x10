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
 * Testing the maxAbs function on arrays.
 */

public class IntArrayMaxAbs extends x10Test {

    public def run(): boolean = {
        val ia  = new Array[int](Region.make(1..10, 1..10), (p:Point)=>(-p(0) as Int));

	    val absMax = ia.reduce((a:Int, b:Int):Int => {
            val ma = Math.abs(a);
            val mb = Math.abs(b);
            ma <= mb? mb : ma
        }, 0n);

	    println("ABSmax=" + absMax);
	    return absMax==10n;
    }

    public static def main(Rail[String]) {
        new IntArrayMaxAbs().execute();
    }
}
