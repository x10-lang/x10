/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

import harness.x10Test;

/**
 * Check that rooted methods can only be called on rooted receivers.
 */
public class RootedCall_MustFailCompile extends x10Test {

    class A {
	var x:Int=1;
	rooted def m() {
	    x=2;
	}
    }

    public def run(): boolean = {
	var a: A = new A();
	a.m(); // must give an error.
	return true;
    }

    public static def main(Rail[String])  {
	new RootedCall().execute();
    }
}
