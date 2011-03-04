/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Non X10 java constructs should be flagged by compiler.
 *
 * @author kemal 4/2005
 */
public class NonX10Constructs_MustFailCompile extends x10Test {
	volatile boolean flag = false;
	int x = 0;
	public boolean run() {
		boolean b = false;
		synchronized(this) { x++; }
		synchronized(this) { b = (x == 1); }
		return b;
	}

	public static void main(String[] args) {
		new NonX10Constructs_MustFailCompile().execute();
	}
}

