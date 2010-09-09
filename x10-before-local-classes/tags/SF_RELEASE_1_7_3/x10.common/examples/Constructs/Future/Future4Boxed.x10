/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * A future test.
 */
public class Future4Boxed extends x10Test {
	public boolean run() {
		future<x10.compilergenerated.BoxedInteger> x = future { new x10.compilergenerated.BoxedInteger(41) };
		return (x.force().intValue()+1) == 42;
	}

	public static void main(String[] args) {
		new Future4Boxed().execute();
	}
}

