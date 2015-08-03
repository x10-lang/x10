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
 * Test operator redefinition. Works only with the C++ backend.
 * @author mandel
 */

class Try3 extends x10Test {

    public static class Id[E]{E <: Exception} {

	val dummy: E;

	public def this(e: E) {
	    dummy = e;
	}

	public operator try(body: ()=>void, hdl:(E)=>void) {
	    try {
		body();
		if (false) { throw dummy; }
	    } catch (e:E) {
		hdl(e);
	    }
	}
    }


    var cpt : Long = 0;
    public def run() : boolean {
	val myExn = new Id(new Exception());

	myExn.try {
	    throw new Exception("Exception 1");
	} catch (Exception) {
	    cpt = cpt + 1;
	}

	chk(cpt == 1);
	return true;
    }

    public static def main(Rail[String]) {
        new Try3().execute();
    }
}
