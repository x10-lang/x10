/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Checks force for grand-children.
 * @author Christoph von Praun
 */
public class FutureForce extends x10Test {

	boolean flag;
	int foo;

	public int bar() {
		System.out.print("waiting ...");
		x10.lang.Runtime.sleep(2000);
		System.out.println("done.");
		atomic flag = true;
		return 42;
	}

	public int foo() {
		future<int> r2 = future(here) { bar() };
		return 42;
	}

	public boolean run() {
		atomic flag = false;
		future<int> r1 = future(here) { foo() };
		r1.force();
		boolean b;
		atomic b = flag;
		System.out.println("The flag is b=" + b + " (should be true).");
		return (b == true);
	}

	public static void main(String[] args) {
		new FutureForce().execute();
	}
}

