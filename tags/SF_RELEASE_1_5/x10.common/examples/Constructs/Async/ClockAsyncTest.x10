/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
/**
 * Code generation for clocked asyn uses "clocks" as the name of the clock list.
 * This test will fail at runtime, because the wrong clocks variable is being used.
 * @author Tong Wen 7/2006
 */
 import harness.x10Test;
public class ClockAsyncTest extends x10Test {

	public boolean run() {
		finish async{
			final clock  value [.] clocks=new clock [[0:5]] (point i)
			{return clock.factory.clock();};
			final int i = 0;
			async (here) clocked (clocks[i]){
				next;
			}
		}
		return true;
	}

	

	public static void main(String[] args) {
		new ClockAsyncTest().execute();
	}
}