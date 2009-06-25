/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Test that creating an array of clocks will not work -- you need to
 * do the following instead:
	    val clocks = Array.make[Clock](0..5, (point)=>null);
	    for ((i) in 0..5) clocks(i) = Clock.make();

 * @author Tong Wen 7/2006
 */
public class ClockAsyncTest extends x10Test {

    public def run(): boolean = {
	try {
	    val clocks = Array.make[Clock](0..5, (Point)=>Clock.make());
	    finish async (here) clocked (clocks(0)){
		next;
	    }
	} catch (x:ClockUseException) {
	    return true;
	}
	return false;
    }

    public static def main(var args: Rail[String]) {
	new ClockAsyncTest().execute();
    }
}
