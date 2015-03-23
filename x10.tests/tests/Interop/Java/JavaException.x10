/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import harness.x10Test;

// MANAGED_X10_ONLY

public class JavaException extends x10Test {
    def x10Exception(): Boolean {
	var pass: Boolean = false;
        try {
            throw new x10.lang.Exception("I like X10");
        } catch (e: x10.lang.Exception) {
	    pass = true;
            //e.printStackTrace();
        }
	return pass;
    }

    def javaException(): Boolean {
	var pass: Boolean = false;
        try {
            throw new x10.lang.CheckedException("I like Java");
        } catch (e: x10.lang.CheckedException) {
	    pass = true;
            //e.printStackTrace();
        }
	return pass;
    }

    public def run(): Boolean {
	var pass: Boolean = false;
        pass = x10Exception();
	chk(pass);
        pass = javaException();
	chk(pass);
        return true;
    }

    public static def main(args: Rail[String]) {
        new JavaException().execute();
    }
}
