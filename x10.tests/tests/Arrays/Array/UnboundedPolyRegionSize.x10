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

import harness.x10Test;
import x10.regionarray.*;
/**
 *Check that an UnboundedRegionException is thrown when you attempt to compute
 *the size of an unbounded region.
 */

class UnboundedPolyRegionSize extends x10Test {

    public def run() {
    	try {
		val r = Region.makeHalfspace(Point.make(1), 0);
		val s = r.size();
		return false;
    	} catch (e: UnboundedRegionException) {
    		return true;
    	}
    }
    public static def main(Rail[String]) {
        new UnboundedPolyRegionSize().execute();
    }
}
