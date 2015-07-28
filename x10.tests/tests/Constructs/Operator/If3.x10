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
 * Test operator redefinition: "if" without "else".
 * @author mandel
 */

class If3 extends x10Test {

    var result: Long = 0;

    public operator if(arg: Long, closure: ()=>Long) {
	result = arg + closure();
    }

    public def run() : boolean {
	this.if (1) {
	    1
	}
	return 2 == this.result;
    }

    public static def main(Rail[String]) {
        new If3().execute();
    }
}
