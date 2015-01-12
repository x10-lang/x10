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
import x10.regionarray.*;

/**
 * Testing exploding syntax for local variables.
 *
 * @author vj 09/02/08
 */
public class ExplodingLocalVar1Test extends x10Test {

	public def run(): boolean = {
	    // the type Point is not supplied. 
	    // This should really not work, according to Sec 4.13.1 (type inference)
	    // and Section 10 (destructuring syntax). But it does.
		val p[x,y]  = Point.make(2, 2);
		return x+y==4L && p(0)+p(1)==4L;
		}

	public static def main(Rail[String]) {
		new ExplodingLocalVar1Test().execute();
	}
}
