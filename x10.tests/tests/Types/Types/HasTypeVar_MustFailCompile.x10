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
 * Cannot specify a hasType for a var field, must specify an actual type.
 * It is ok for this test to generate the error "Cannot infer type of non-final field."
 *
 * @author vj
 */
public class HasTypeVar_MustFailCompile extends x10Test {
	
	public def run() {
		var x <: Long = 1; // ERR: Cannot infer type of a mutable (non-val) variable.
		return true;
	}

	public static def main(Rail[String])  {
		new HasTypeVar_MustFailCompile().execute();
	}
}
