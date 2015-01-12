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
 * Purpose: Shows a constraint value may be overflowed.
 * Issue: Declared constraint is an overflowed integer, which makes assignment fail at runtime.
 * @author vcave, vj
 **/
public class Long_ConstrainedDeclaredAsIntegerOverflow_MustFailCompile extends x10Test {

	 private val overIntMax: long = (x10.lang.Int.MAX_VALUE as long) + 10000;
	 
	 public def run(): boolean = {
		try {
			// This value cannot fit in an integer, so the compiler must flag an error.
			var l2: long{self==2147493647L} = overIntMax as long{self==2147493647n}; // ERR: Int literal 2147493647 is out of range. // ERR Cannot build constraint from expression |x10.lang.Long.self == x10.lang.Long.implicit_operator_as(-2147473649)
		} catch (var e: ClassCastException) {
			return true;
		}
		return false;
	}

	public static def main(var args: Rail[String]): void = {
		new  Long_ConstrainedDeclaredAsIntegerOverflow_MustFailCompile().execute();
	}

}
