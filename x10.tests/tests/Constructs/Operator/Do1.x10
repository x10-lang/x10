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

class Do1 extends x10Test {

    static class Parallel {
	public static operator do (body:()=>void, cond:()=>Boolean) {
	    finish {
		do {
		    async { body(); }
		} while (cond());
	    }
	}

    }

    var cpt : Long = 0;
    public def run() : boolean {
	val iterator = (0 .. 10).iterator();
	Parallel.do {
	    atomic { cpt = cpt + 1; }
	} while( ()=> { iterator.next(); iterator.hasNext() } );
	chk(cpt == 11);
	return true;
    }

    public static def main(Rail[String]) {
        new Do1().execute();
    }
}
