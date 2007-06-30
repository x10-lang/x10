/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Static mutable fields are not allowed in x10.
 *
 * (leads to complexities in defining the place of static
 * fields of a class).
 *
 * @author kemal 5/2005
 */
public class NoStaticMutable1_MustFailCompile extends x10Test {
	//<== compiler error must occur on next line
	static int x1 = 0;

	static final int x2 = 0;
	const int x3 = 0;

	//<== compiler error must occur on next line
	static foo f1 = new foo(1);

	final static foo f2 = new foo(1);
	const foo f3 = new foo(1);

	public boolean run() {
		x1++;
		f1 = new foo(2);
		return true;
	}

	public static void main(String[] args) {
		new NoStaticMutable1_MustFailCompile().execute();
	}

	static class foo {
		int val;
		foo(int x) { val = x; }
	}
}

