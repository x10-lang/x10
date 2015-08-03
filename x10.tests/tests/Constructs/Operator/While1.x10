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

class While1 extends x10Test {

    static class Parallel {
	public static operator while (cond:()=>Boolean, body: ()=>void) {
	    finish {
		while (cond()) {
		    async { body(); }
		}
	    }
	}
    }

    var cpt : Long = 0;
    public def run() : boolean {
	val iterator = (0 .. 10).iterator();
	Parallel.while( ()=> { iterator.next(); iterator.hasNext() } ) {
	    atomic { cpt = cpt + 1; }
	}
	chk(cpt == 10);
	return true;
    }

    public static def main(Rail[String]) {
        new While1().execute();
    }
}
