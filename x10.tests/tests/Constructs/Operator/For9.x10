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

class For9 extends x10Test {

    static class Id {
	public static operator for[T](set: Iterable[T], body: (T)=>void) {
	    for (x in set) {
		body(x);
	    }
	}
    }

    public def run() : boolean {
	val x = new Cell[Long](0);
	Id.for[Long](i:Long in 1..10) {
	    x() = x() + i;
	}
	return 55 == x();
    }

    public static def main(Rail[String]) {
        new For9().execute();
    }
}
