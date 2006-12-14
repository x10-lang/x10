/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Automatic boxing and unboxing of a final value class
 * during up-cast and down-cast.
 */
public class Boxing0 extends x10Test {

	public boolean run() {
		x10.lang.Object o = X.five();
		int i = (int) o + 1;
		return (i == 6);
	}

	public static void main(String[] args) {
		new Boxing0().execute();
	}

	static class X {
		public static int five() { return 5; }
	}
}

