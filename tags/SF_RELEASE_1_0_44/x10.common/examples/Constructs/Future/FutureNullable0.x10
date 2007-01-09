/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Given: future nullable T x;
 * x.force() is of type nullable T and can be null
 */
public class FutureNullable0 extends x10Test {
	public boolean run() {
		future<nullable<FutureNullable0>> x = future(here) { null };
		return (x.force()) == null;
	}

	public static void main(String[] args) {
		new FutureNullable0().execute();
	}
}

