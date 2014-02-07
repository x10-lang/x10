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

/**
 * Check hastypes.
 *
 * @author vj
 */
public class HasTypeMethod_MustFailCompile extends x10Test {

	def m(x:Long{self==1}) <: Boolean {return x;} // ERR: Computed type is not a subtype of type bound.
	public def run() = true;

	public static def main(Rail[String])  {
		new HasTypeMethod_MustFailCompile().execute();
	}
}
