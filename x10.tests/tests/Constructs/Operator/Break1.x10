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

class Break1 extends x10Test {

    static class Seq {
	private static class Break extends Exception {}

	public static operator for[T](c: Iterable[T], body:(T)=>void) {
	    try {
		for(i in c) {
		    body(i);
		}
	    } catch (Break) {}
	}

	public static operator break () throws Break {
	    throw new Break();
	}
    }

    var cpt : Long = 0;
    public def run() : boolean {
	Seq.for (i:Long in 1..100) {
	    if (i > 10) { Seq.break; }
	    cpt = i + cpt;
	}
	chk(cpt == 55);
	return true;
    }

    public static def main(Rail[String]) {
        new Break1().execute();
    }
}
