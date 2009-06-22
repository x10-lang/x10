/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Checks if finish also consideres grand-children.
 * @author Christoph von Praun
 */
public class FinishTest2 extends x10Test {

	boolean flag;
	int foo;

	public boolean run() {
		atomic flag = false;
		finish {
			async (here) {
				atomic foo = 123;
				async (here) {
					atomic foo = 42;
					System.out.print("waiting ...");
					x10.lang.Runtime.sleep(2000);
					System.out.println("done.");
					atomic flag = true;
				}
			}
		}
		boolean b;
		atomic b = flag;
		System.out.println("The flag is b = " + b + " (should be true).");
		return (b == true);
	}

	public static void main(String[] args) {
		new FinishTest2().execute();
	}
}

