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

class For10 extends x10Test {

    var result: Long = 0;

    public operator for(interval: Iterable[Long], body: (Long)=>Long) {
	var acc: Long = 0;
	for (i:Long in interval) {
	    acc = acc + body(i);
	}
	result = acc;
	return acc;
    }

    static class Static {
        public static operator for(interval: Iterable[Long], body: (Long)=>Long) {
	    var acc: Long = 0;
	    for (i:Long in interval) {
		acc = acc + body(i);
	    }
	    return acc;
	}
    }

    public def run() : boolean {
	this.for(i:Long in 1..10) {
	    i
	}
	val r1 = this.result;
	val r2 = operator for(1..10, ((i:Long)=>i));
	val r3 = Static.operator for(1..10, ((i:Long)=>i));
	val r4 = this.operator for(1..10, ((i:Long)=>i));
	chk(r1 == 55);
	chk(r1 == r2);
	chk(r1 == r3);
	chk(r1 == r4);
	return true;
    }

    public static def main(Rail[String]) {
        new For10().execute();
    }
}
