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

//OPTIONS: -STATIC_CHECKS

import harness.x10Test;

/**
 * Purpose: Checks an overflow is detected when a constant is assigned.
 * @author vcave
 */
public class Long_ConstrainedDeclaredAsIntegerOverflow2 extends x10Test {

	 public def run(): boolean = {
		var result: boolean = false;
		val notAnInt: long = 2147493648L ;
		val b: int{self == -2147473648n} = notAnInt as int{self== -2147473648n};
		x10.io.Console.OUT.println("" + " bound=" + b
				+ " notAnInt=" + notAnInt);
		try {
		//  this time constraint is a long but value to assign is an overflowed integer
		// Hence at compile time we can state contraint value is != from constant.
		var l3: long{self==2147493648L} = b as long{self==2147493648L};
		} catch (e: ClassCastException) {
			return true;
		}
		return false;
	}

	public static def main(var args: Rail[String]): void = {
		new  Long_ConstrainedDeclaredAsIntegerOverflow2().execute();
	}
}
