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

//OPTIONS: -STATIC_CHECKS

import harness.x10Test;
import x10.compiler.*; // @Uncounted @NonEscaping @NoThisAccess
import x10.compiler.tests.*; // err markers

/**
 * Purpose: Check primitive variable assignment to primitive dependent type.
 * Issue: Variable j is statically known as an int. Conversion from int as int(:c) is forbidden.
 * @author vcave
 **/
public class AssignmentPrimitiveToPrimitiveConstrained_MustFailCompile extends x10Test {

	public def run(): boolean = {
		
		try { 
			var i: int{self == 0n} = 0n;
			var j: int = 0n;
			// Even if j equals zero, types are not compatible
			// A cast would be necessary to check conversion validity
			i = @ERR j;
		}catch(e: Exception) {
			return false;
		}

		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new AssignmentPrimitiveToPrimitiveConstrained_MustFailCompile().execute();
	}

}
