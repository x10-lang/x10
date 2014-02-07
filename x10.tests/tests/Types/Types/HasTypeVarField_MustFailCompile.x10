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
 * Cannot specify a hasType for a var field, must specify an actual type.
 * It is ok for this test to generate the error "Cannot infer type of non-final field."
 *
 * @author vj
 */
public class HasTypeVarField_MustFailCompile extends x10Test {
	var x <: Long = 1; // ERR ERR [Semantic Error: Cannot infer type of non-final fields., Semantic Error: Only val fields may have a has type.]
	
	public def run()=true;

	public static def main(Rail[String])  {
		new HasTypeVarField_MustFailCompile().execute();
	}
}
