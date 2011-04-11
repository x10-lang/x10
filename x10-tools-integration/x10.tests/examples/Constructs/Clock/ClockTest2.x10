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
 * Test for 'now'.  Very likely to fail if now is not translated
 * properly (but depends theoretically on the scheduler).
 */
public class ClockTest2 extends x10Test {

	var value: int = 0;
	static  N: int = 10;

	public def run(): boolean = {
		clocked finish {
		   for (var i: int = 0; i < N; i++) {
			 clocked 
			    async   
					atomic 
						value++;
			  Clock.advanceAll();
			  var temp: int;
			  atomic { temp = value; }
			  if (temp != i+1) return false;
		    }
		}
		return true;
	}

	public static def main(Array[String](1))  {
		new ClockTest2().executeAsync();
	}
}
