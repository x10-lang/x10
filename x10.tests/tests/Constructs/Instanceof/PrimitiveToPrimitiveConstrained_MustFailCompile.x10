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
 * Purpose: Checks a boxed litteral primitive is effectively checked against
 *          a constrained primitive type.
 * @author vcave
 **/
public class PrimitiveToPrimitiveConstrained_MustFailCompile extends x10Test {
	 
	public def run() =
	    3n instanceof int{self==4n}; // ERR
	
	public static def main(var args: Rail[String]): void = {
		new PrimitiveToPrimitiveConstrained_MustFailCompile().execute();
	}
}
