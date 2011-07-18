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
 * Purpose: Checks constant promotion works.
 * Note: The compiler promotes constant's type from int as int(:self==0)
 * @author vcave
 **/
public class CastPrimitiveLitteralToPrimitiveConstrained1 extends x10Test {

	public def run(): boolean = {
		var i: int{self == 0} = 0;
		i = 0 as int{self == 0};
		return true;
	}

	public static def main(var args: Array[String](1)): void = {
		new CastPrimitiveLitteralToPrimitiveConstrained1().execute();
	}

}
