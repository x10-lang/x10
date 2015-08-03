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

class Continue1 extends x10Test {

    static class Parallel {

	private static class Continue extends Exception {}

	public static operator for[T](c: Iterable[T], body:(T)=>void) {
	    for(x in c) {
		try {
		    body(x);
		} catch (Continue) {}
	    }
	}


	public static operator continue () {
	    throw new Continue();
	}
    }

    public def run() : boolean {
	val cpt = new Cell[Long](0);
	Parallel.for(i:Long in 1..10) {
	    if (i%2 == 0) { Parallel.continue; }
	    atomic { cpt() = cpt() + 1; }
	}
	chk(cpt() == 5);
	return true;
    }

    public static def main(Rail[String]) {
        new Continue1().execute();
    }
}
