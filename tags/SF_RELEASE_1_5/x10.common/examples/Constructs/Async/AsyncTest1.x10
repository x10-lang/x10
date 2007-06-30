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

	boolean flag;
	public boolean run() {
		async (this) { atomic { flag = true; } }
		boolean b;
		do {
			atomic { b = flag; }
		} while (!b);
		return b;
	}

	public static void main(String[] args) {
		new AsyncTest1().execute();
	}
}

