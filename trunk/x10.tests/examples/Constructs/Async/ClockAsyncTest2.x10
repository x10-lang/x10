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
 * Code generation for clocked async uses "clocks" as the name of the clock
 * list.
 * This test will fail at compile time because the wrong clocks variable is being used.
 * javac compiler error:  array required, but java.util.LinkedList found
 * @author Tong Wen 7/2006
 * @author vj 7/2006
 */
public class ClockAsyncTest2 extends x10Test {

	public def run(): boolean = {
	   finish async {
	      val clocks: Rail[Clock]! = [ Clock.make() ];
	      async (here) clocked (clocks(0)){
		    next;
	      }
	    }
	    return true;
        }

	

	public static def main(var args: Rail[String]): void = {
		new ClockAsyncTest2().execute();
	}
}
