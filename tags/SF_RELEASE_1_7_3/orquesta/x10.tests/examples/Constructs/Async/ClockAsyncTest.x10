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
 * This test will fail at runtime, because the wrong clocks variable is being used.
 * @author Tong Wen 7/2006
 */
 import harness.x10Test;;
public class ClockAsyncTest extends x10Test {

	public def run(): boolean = {
		finish async{
			val clocks: Array[Clock] = new Array[Clock]([0..5], ((i): point): Clock => Clock.make());
			val i: int = 0;
			async (here) clocked (clocks(i)){
				next;
			}
		}
		return true;
	}

	

	public static def main(var args: Rail[String]): void = {
		new ClockAsyncTest().execute();
	}
}
