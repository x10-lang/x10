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
 * Purpose: Cast's dependent type constraint must be satisfied by the primitive.
 * Issue: Value to cast does not meet constraint requirement of target type.
 * @author vcave
 **/
public class AssignmentPrimitiveConstrainedToPrimitiveConstrained_MustFailCompile extends x10Test {

	public def run(): boolean = {
		
		try { 
			var i: int{self == 1} = 1;
			var j: int{self == 0} = i; // ERR
		} catch(e: Throwable) {
			return false;
		}

		return true;
	}

	public static def main(var args: Array[String](1)): void = {
		new AssignmentPrimitiveConstrainedToPrimitiveConstrained_MustFailCompile().execute();
	}

}
