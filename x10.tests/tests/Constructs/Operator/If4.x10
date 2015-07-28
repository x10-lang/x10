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
 * Test operator redefinition. We can do wired call of the if.
 * @author mandel
 */

class If4 extends x10Test {

    static class Id {
	public static operator if (b: Boolean, then: ()=>void, else_:()=>void) {
	    if (b) {
		then();
	    } else {
		else_();
	    }
	}
    }

    var cpt: Long = 0;
    public def run() : boolean {
	Id.if (true) { cpt = 1; } else { cpt = 2; };
	chk(cpt == 1);
	Id.if (true, () => {cpt = 3;} ) { cpt = 4; };
	chk(cpt == 3);
	return true;
    }

    public static def main(Rail[String]) {
        new If4().execute();
    }
}
