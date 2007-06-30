/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Minimal test for finish, using an async.
 * run() method returns true if successful, false otherwise.
 * @author Vivek Sarkar (vsarkar@us.ibm.com)
 */
public class FinishTest1 extends x10Test {

	boolean flag;

	public boolean run() {
		finish {
			async (here) { atomic { flag = true; } }
		}
		boolean b;
		atomic { b = flag; }
		return b;
	}

	public static void main(String[] args) {
		new FinishTest1().execute();
	}
}

