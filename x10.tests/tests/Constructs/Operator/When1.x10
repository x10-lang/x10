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

class When1 extends x10Test {

    static class CancelableWhen {
	private var stop : Boolean = false;

	public operator when(condition: ()=>Boolean, body: ()=> void) {
	    when (condition() || stop) {
		if (!stop) {
		    body();
		}
	    }
	}

	public def cancel() {
	    atomic { stop = true; }
	}
    }

    var cpt : Long = 0;
    var go : Boolean = false;
    public def run() : boolean {
	val a = new CancelableWhen();
	finish {
	    for (i in 1..1000) {
		async {
		    a.when(()=>go) {
			cpt = cpt + 1;
		    }
		}
	    }
	    atomic { go = true; }
	}
	finish {
	    async {
		a.when (()=> false) {
		    cpt = 0;
		}
	    }
	    a.cancel();
	}
	return cpt == 1000;
    }

    public static def main(Rail[String]) {
        new When1().execute();
    }
}
