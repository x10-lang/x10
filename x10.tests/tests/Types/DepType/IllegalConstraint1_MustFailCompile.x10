/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

import harness.x10Test;

/**
* Checking that the type-checker can correctly handle boolean expressions as the values
* boolean properties.

 */

public class IllegalConstraint1_MustFailCompile  extends x10Test {

	class C(a:boolean) {
		static type C(b:boolean) = C{self.a==b};
		def n(x:C, y:C) {
			val z1: C(x.a&&y.a) = new C(x.a && y.a); // ERR
		}
	}
    public def run() = true;

    public static def main(Rail[String])  {
        new IllegalConstraint1_MustFailCompile().execute();
    }
}
