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

class For3 extends x10Test {

    static class CollectingFor[T]{T haszero} {
	val reduce: (a:T,b:T)=>T;
	var result: T;
	def this(r: (a:T,b:T)=>T) { reduce = r; }
	operator for (range: LongRange, body: (Long) => T) {
	    val res = new Rail[T](range.max - range.min + 1);
	    finish for (i in range) async { res(i-range.min) = body(i); }
	    for (i in range) result = reduce(result, res(i-range.min));
	}
    }

    public def run() : boolean {
	val collect = new CollectingFor[Long]((x: Long, y: Long) => x + y);
	collect.for (i : Long in 0 .. 10) { i }
	return 55 == collect.result;
    }

    public static def main(Rail[String]) {
        new For3().execute();
    }
}
