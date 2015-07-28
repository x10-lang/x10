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

/**
 * Test operator redefinition.
 * @author mandel
 */

class At1 extends x10Test {


    static class RoundRobin {
	val places: Rail[Place];
	public def this (places: Rail[Place]) {
	    this.places = places;
	}

	private var next: Long = 0;
	public operator at(body: ()=>void) {
	    val p : Place;
	    atomic {
		p = places(next);
		next = (next + 1) % places.size;
	    }
	    at(p) { body(); }
	}
    }

    public def run() : boolean {
	val rr = new RoundRobin([here as Place] as Rail[Place]);
	rr.at() {
	}
	return true;
    }

    public static def main(Rail[String]) {
        new At1().execute();
    }
}
