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
import x10.array.*;

// NUM_PLACES: 4

/**
 * @author bdlucas
 */
public class ArrayMap2 extends x10Test {

    public static N: long = 9;

    public def run(): boolean {
        val a = new DistArray_Block_1[Long](100, (i:long)=>i);
	val b = new DistArray_Block_1[Long](100, (i:long)=>10*i);
	val c = new DistArray_Block_1[Long](100);
	val d = new DistArray_Block_1[Boolean](100);

	a.map(c, (x:long)=>10*x);
        b.map(c, d, (x:long,y:long)=> x==y);
	
	for (p in d.placeGroup()) at (p) {
            for (pt in d.localIndices()) {
                chk(d(pt));
            }
        }
	
	// redundant with pointwise check above, but acts to check reduce too...
        return d.reduce((a:Boolean,b:Boolean)=>a&&b, true);
    }

    public static def main(var args: Rail[String]):void {
        new ArrayMap2().execute();
    }
}
