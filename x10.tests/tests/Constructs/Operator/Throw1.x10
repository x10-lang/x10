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

class Throw1 extends x10Test {

    static class Id {
	public static operator throw[Exn](exn: Exn){CheckedThrowable :> Exn} throws Exn {
	    throw exn;
	}
    }

    public def run() : boolean {
	var b : Boolean = false;
	try {
	    Id.throw[Exception] new Exception("Exn");
	} catch (e: Exception) {
	    b = true;
	}
	chk(b);
	return true;
    }

    public static def main(Rail[String]) {
        new Throw1().execute();
    }
}
