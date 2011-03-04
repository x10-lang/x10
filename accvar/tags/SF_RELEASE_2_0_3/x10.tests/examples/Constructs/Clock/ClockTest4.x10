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
 * Clock test for barrier functions.
 * foreach loop body represented with a method.
 *
 * @author kemal 3/2005
 */
public class ClockTest4 extends x10Test {

	var val: int = 0;
	public const N: int = 32;

	public def run(): boolean = {
		val c: Clock = Clock.make();

		foreach (val (i): Point in 1..(N-1)) clocked(c) {
			foreachBody(i, c);
		}
		foreachBody(0, c);
		var temp2: int;
		atomic { temp2 = val; }
		chk(temp2 == 0);
		return true;
	}

	def foreachBody(val i: int, val c: Clock): void = {
		async(here) clocked(c) finish async(here) { async(here) { atomic val += i; } }
		next;
		var temp: int;
		atomic { temp = val; }
		chk(temp == N*(N-1)/2);
		next;
		async(here) clocked(c) finish async(here) { async(here) { atomic val -= i; } }
		next;
	}

	public static def main(var args: Rail[String]): void = {
		new ClockTest4().executeAsync();
	}
}
