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
 * Clock test for barrier functions.
 * foreach loop body represented with a method.
 *
 * @author kemal 3/2005
 */
public class ClockTest4 extends x10Test {

	var value: long = 0;
	public static N: long = 32;

	public def run(): boolean = {
		val c: Clock = Clock.make();

		for  (i in 1..(N-1)) async clocked(c) {
			foreachBody(i, c);
		}
		foreachBody(0, c);
		var temp2: long;
		atomic { temp2 = value; }
		chk(temp2 == 0);
		return true;
	}

	def foreachBody(i: long, c: Clock): void = {
		async clocked(c) finish async { async { atomic value += i; } }
		Clock.advanceAll();
		var temp: long;
		atomic { temp = value; }
		chk(temp == N*(N-1)/2);
		Clock.advanceAll();
		async clocked(c) finish async { async { atomic value -= i; } }
		Clock.advanceAll();
	}

	public static def main(Rail[String]) {
		new ClockTest4().executeAsync();
	}
}
