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
 * Minimal test for when.
 */
public class AwaitTest extends x10Test {

	var val_: int = 0;

	public def run(): boolean = {
		val c: Clock = Clock.make();
		async clocked(c) {
			when(val_ > 43);
			atomic val_ = 42;
			when(val_ == 0);
			atomic val_ = 42;
		}
		atomic val_ = 44;
		when (val_ == 42);
		var temp: int;
		atomic temp = val_;
		//x10.io.Console.OUT.println("temp = " + temp);
		if (temp != 42)
			return false;
		atomic val_ = 0;
		when (val_ == 42);
		Clock.advanceAll();
		var temp2: int;
		atomic temp2 = val_;
		//x10.io.Console.OUT.println("val_ = " + temp2);
		return temp2 == 42;
	}

	public static def main(Rail[String]){
		new AwaitTest().executeAsync();
	}
}
