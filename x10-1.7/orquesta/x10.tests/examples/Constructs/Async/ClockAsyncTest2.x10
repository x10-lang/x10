/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
/**
 * Code generation for clocked async uses "clocks" as the name of the clock
 * list.
 * This test will fail at compile time because the wrong clocks variable is being used.
 * javac compiler error:  array required, but java.util.LinkedList found
 * @author Tong Wen 7/2006
 * @author vj 7/2006
 */
 import harness.x10Test;;
public class ClockAsyncTest2 extends x10Test {

	public def run(): boolean = {
	   finish async {
	      val clocks: Rail[clock] = [ Clock.make() ];
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
