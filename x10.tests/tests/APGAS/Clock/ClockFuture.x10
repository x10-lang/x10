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

import x10.util.concurrent.Future;
/**
 * Test for the interaction of clocks and future.
 * clock.doNext should not wait for futures to
 * terminate.
 * @author Christoph von Praun
 */
public class ClockFuture extends x10Test {

    private var clock_has_advanced: boolean;

    public def m(): long = {
	   var ret: long = 0;
	   when (clock_has_advanced) {
	      ret = 42;
	   }
	   return ret;
    }

    public def run(): boolean = {
	   clocked finish { 
         val f  = Future.make[long] (()=> m());
         // x10.io.Console.OUT.print("1 ... ");
         // this next should not wait on the future
         Clock.advanceAll();
         // x10.io.Console.OUT.print("2 ... ");
         atomic { clock_has_advanced = true; }
         // x10.io.Console.OUT.print("3 ...");
         var result: long = f();
         chk(result == 42);
         // x10.io.Console.OUT.println("4");
	   }
       return true;
    }

    public static def main(Rail[String]) {
	   new ClockFuture().execute();
    }
}
