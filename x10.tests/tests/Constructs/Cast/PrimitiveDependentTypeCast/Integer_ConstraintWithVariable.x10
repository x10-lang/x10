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
 * Purpose: Checks constraint's variable are resolved.
 * @author vcave
 **/
public class Integer_ConstraintWithVariable extends x10Test {

	public def run(): boolean = {
		val iconstraint: int{self==0n} = 0n;
		// constraint's variable must be final
		// hence these two types should be equivalent
		var i1: int{self == iconstraint} = 0n;
		var i2: int{self==iconstraint} = 0n as int{self==iconstraint};
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new Integer_ConstraintWithVariable().execute();
	}


}
