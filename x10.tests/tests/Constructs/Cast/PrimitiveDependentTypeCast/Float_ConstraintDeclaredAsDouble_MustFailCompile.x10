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
import x10.compiler.*; // @Uncounted @NonEscaping @NoThisAccess
import x10.compiler.tests.*; // err markers

/**
 * Purpose: Illustrates float dependent type usage and Checks the numeric 
 *          expression to cast is not evaluated several time while checking for constraint
 * Note: The cast should not be inlined to avoid several execution of j*=2
 * Note: Append an 'F' force constraint representation to be a float.
 * @author vcave
 **/
public class Float_ConstraintDeclaredAsDouble_MustFailCompile extends x10Test {

	public def run(): boolean = {
		var j: float = 0.00001F;
		
		// invalid assignment 
		// float/*(:{self=2.0E-5F})*/ <-- double/*(:{self=2.0E-5})*/  
		@ERR var i1: float{self == 0.00002F} = 0.00002;

		// invalid assignment 
		// float/*(:{self=2.0E-5F})*/ <-- double/*(:{self=2.0E-5})*/  
		@ERR var i2: float{self == 0.00002F} = 0.00002D;

		// invalid assignment 
		// float/*(:{self=2.0E-5F})*/ <-- float
		var i3: float{self == 0.00002F} = 0.00002 as float;

		// invalid assignment 
		// float/*(:{self=2.0E-5F})*/ <-- float
		var i4: float{self == 0.00002F} = 0.00002D as float;

		// float/*(:{self=2.0E-5F})*/ <-- float
		var i5: float{self == 0.00002F} = 0.00002F as float; // ok!
		var i6: float{self == 0.00002F} = 0.00002F;
		
		return false;
	}

	public static def main(var args: Rail[String]): void = {
		new Float_ConstraintDeclaredAsDouble_MustFailCompile().execute();
	}

}
