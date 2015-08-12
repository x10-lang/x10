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
import x10.array.DenseIterationSpace_2;

/**
 * Test operator redefinition.
 * @author mandel
 */

class For11 extends x10Test {

    public static operator for (space: DenseIterationSpace_2,
				body: (i:Long, j:Long)=>void) {
	for ([i, j] in space) {
	    body(i, j);
	}
    }

    public def run() : boolean {
	val cpt = new Cell[Long](0);
	this.for (i:Long, j:Long in 1..10 * 1..10) {
	    cpt() = cpt() + 1;
	}
	return 100 == cpt();
    }

    public static def main(Rail[String]) {
        new For11().execute();
    }
}
