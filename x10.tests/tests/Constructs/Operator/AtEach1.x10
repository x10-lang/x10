/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 *  (C) Copyright Australian National University 2009-2010.
 */

import harness.x10Test;
import x10.util.*;
import x10.regionarray.*;

/**
 * Test operator redefinition.
 * @author mandel
 */

class AtEach1 extends x10Test {


    static class Sequential {
	public static operator ateach (d: Dist, body:(Point)=>void) {
	    for (place in d.places()) {
		at(place) {
		    for (p in d|here) {
			body(p);
		    }
		}
	    }
	}
    }

    public def run() : boolean {
	val d: Dist = Dist.makeUnique();
	Sequential.ateach(p:Point in d) {
	}
	return true;
    }

    public static def main(Rail[String]) {
        new AtEach1().execute();
    }
}
