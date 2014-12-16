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
 * Test that creating an array of clocks will not work -- you need to
 * do the following instead:
	    val clocks = Array.make[Clock](6, null);
	    for (i in 0..5) clocks(i) = Clock.make();

 * @author Tong Wen 7/2006
 * 9/23/2010
 * vj: Rewrote this test. The above idiom will work. However, the activity
 * executing the test is registered on all the clocks. So for the finish to terminate
 * the main activity has to drop the clock. Standard finish/clock interaction.
 */
public class ClockAsyncTest extends x10Test {

    public def run(): boolean {
	  try {
	    val clocks = new Rail[Clock](6, (i:long)=>Clock.make());
	    finish {
	    	async clocked (clocks(0)){
	    		Clock.advanceAll();
	        }
	    	clocks(0).drop();
	    }
	  } catch (x:ClockUseException) {
	    return false;
	  }
	  return true;
    }

    public static def main(Rail[String]) {
	   new ClockAsyncTest().execute();
    }
}
