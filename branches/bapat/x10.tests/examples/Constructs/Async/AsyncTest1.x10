/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Minimal test for async.
 * Uses busy-wait to check for execution of async.
 * run() method returns true if successful, false otherwise.
 *
 * @author Vivek Sarkar (vsarkar@us.ibm.com)
 */
public class AsyncTest1 extends x10Test {

	var flag: boolean;
	public def run(): boolean = {
		async (this.location) { atomic { flag = true; } }
		var b: boolean;
		do {
			atomic { b = flag; }
		} while (!b);
		return b;
	}

	public static def main(var args: Rail[String]): void = {
		new AsyncTest1().execute();
	}
}
