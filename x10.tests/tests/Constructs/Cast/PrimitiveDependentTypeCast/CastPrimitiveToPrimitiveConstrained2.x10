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
 * Purpose: Cast's dependent type constraint must be satisfied by the primitive.
 * @author vcave
 **/
public class CastPrimitiveToPrimitiveConstrained2 extends x10Test {

	public def run(): boolean {
	
		var i: int{self == 0n} = 0n;
		var j: int = 0n;
		i = j as int{self == 0n};

		return true;
	}

	public static def main(var args: Rail[String]): void {
		new CastPrimitiveToPrimitiveConstrained2().execute();
	}

}
