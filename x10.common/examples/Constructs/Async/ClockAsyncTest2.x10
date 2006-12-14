/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
/**
 * Code generation for clocked asyn uses "clocks" as the name of the clock list.
 * This test will fail at compile time because the wrong clocks variable is being used.
 * javac compiler error:  array required, but java.util.LinkedList found
 * @author Tong Wen 7/2006
 * @author vj 7/2006
 */
 import harness.x10Test;
public class ClockAsyncTest2 extends x10Test {

	public boolean run() {
	   finish async {
	      final clock [] clocks=new clock [] { clock.factory.clock()};
	      async (here) clocked (clocks[0]){
		    next;
	      }
	    }
	    return true;
        }

	

	public static void main(String[] args) {
		new ClockAsyncTest2().execute();
	}
}