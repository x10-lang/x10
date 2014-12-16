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
 * Purpose: Checks regular java cast works.
 * @author vcave
 **/
public class CastLitteralToPrimitive extends x10Test {

	public def run(): boolean = {
		var boolt: boolean = true as boolean;
		var boolf: boolean = false as boolean;
		var b: byte = 1 as byte;
		var i: int = 1 as int;
		var s: short = 1 as short;
		var l: long = 1 as long;
		var d: double = 1.0 as double;
		var f: float = 1.0 as float;
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new CastLitteralToPrimitive().execute();
	}
}
