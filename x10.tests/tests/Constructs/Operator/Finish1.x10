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

class Finish1 extends x10Test {

    static class SignalingFinish {
	private var terminated : Boolean = false;
	public operator finish(body: () => void) {
	    finish {
		body();
	    }
	    atomic { terminated = true; }
	}

	public def join() {
	    when (terminated) {}
	}

    }

    var cpt: Long = 0;
    public def run() : boolean {
	finish {
	    val a = new SignalingFinish();
	    async {
		a.join();
		chk(cpt == 1000);
	    }
	    a.finish {
		for (i in 1..1000) {
		    async {
			atomic { cpt = cpt + 1; }
		    }
		}
	    }
	}
	return cpt == 1000;
    }

    public static def main(Rail[String]) {
        new Finish1().execute();
    }
}
