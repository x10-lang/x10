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

class If2 extends x10Test {

    class Rand {
	val random = new Random();
	public operator if(then: ()=>void, else_: ()=>void) {
	    if (random.nextBoolean()) {
		then();
	    } else {
		else_();
	    }
	}
    }

    var left: Boolean = false;
    var right: Boolean = false;

    public def run() : boolean {
	val rand = new Rand();
	rand.if () { this.left = true; } else { this.right = true; }
	return (this.left && !this.right) || (!this.left && this.right);
    }

    public static def main(Rail[String]) {
        new If2().execute();
    }
}
