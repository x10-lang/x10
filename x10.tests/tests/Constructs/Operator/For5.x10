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

class For5 extends x10Test {

    var sum: Long = 0;
    public operator for(interval: Iterable[Long], body: (Long)=>Long) {
        for(i in interval) {
	    sum = sum + body(i);
	}
    }

    public def run() : boolean {
	this.for (i : Long  in 1..10) {
	    i
	}
	return 55 == sum;
    }

    public static def main(Rail[String]) {
        new For5().execute();
    }
}
