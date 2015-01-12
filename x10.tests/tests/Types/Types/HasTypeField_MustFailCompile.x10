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
 * Check the hasType of a field is implied by the actual type.
 *
 * @author vj
 */
public class HasTypeField_MustFailCompile extends x10Test {
	val x <: Boolean = 1; // ERR
	
	public def run()=true;

	public static def main(Rail[String])  {
		new HasTypeField_MustFailCompile().execute();
	}
}
