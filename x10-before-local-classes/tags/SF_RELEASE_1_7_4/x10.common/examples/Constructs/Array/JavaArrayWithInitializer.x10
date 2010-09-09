/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * The x10-style initializer for java arrays should work.
 *
 * @author kemal 8/2005
 */
public class JavaArrayWithInitializer extends x10Test {

	const int N = 25;

	public boolean run() {
		int[.] foo1 = new int[[0:N-1]] (point [i]) { return i; };
		System.out.println("1");
		for (point [i]: [0:N-1]) chk(foo1[i] == i);
		int[] foo2 = new int[N] (point [i]) { return i; };
		System.out.println("2");
		for (point [i]: [0:N-1]) chk(foo2[i] == i);
		return true;
	}

	public static void main(String[] args) {
		new JavaArrayWithInitializer().execute();
	}
}

