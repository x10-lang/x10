/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
//LIMITATION:
//This test case will not meet expectations. It is a limitation of the current release.
import harness.x10Test;

/**
 * A test for bad place exceptions
 *
 * @author Christoph von Praun
 */
public class Exceptions_BadPlace extends x10Test {

	const int M = 10;
	public boolean run() {
		if (place.MAX_PLACES == 1) return true;

		boolean gotException;

		final BoxedBoolean[.] B = new BoxedBoolean[dist.factory.block([0:M-1])]
			(point [i]) { return new BoxedBoolean(); };
		gotException = false;
		try {
			for (point [i]: B) { B[i].val = true; }
		} catch (BadPlaceException e) {
			gotException = true;
		}
		System.out.println("1");
		if (!gotException) return false;

		final double[.] A = new double[dist.factory.block([0:M-1])];
		gotException = false;
		try {
			for (point [i]: A) { A[i] = 1.0; }
		} catch (BadPlaceException e) {
			gotException = true;
		}
		System.out.println("2");
		if (!gotException) return false;

		final boxedInt value[.] VB = new boxedInt value[dist.factory.block([0:M-1])]
			(point [i]) { return new boxedInt(); };
		gotException = false;
		try {
			int x = 0;
			for (point [i]: VB) {  x |= VB[i].val; }
		} catch (BadPlaceException e) {
			gotException = true;
		}
		System.out.println("3");
		if (gotException) return false;
		// CVP -- fails if the boxedInt class is a reference, not a value class
		// hence a BadPlaceException occurs on access.

		final double value[.] VA = new double value[dist.factory.block([0:M-1])];
		gotException = false;
		try {
			double x = 0.0;
			for (point [i]: VA) { x += VA[i]; }
		} catch (BadPlaceException e) {
			gotException = true;
		}
		System.out.println("4");
		if (gotException) return false;

		return true;
	}

	public static void main(String[] args) {
		new Exceptions_BadPlace().execute();
	}

	static class BoxedBoolean {
		boolean val = false;
	}

	// CVP -- if this class is declared as value class, then case 3 succeeds.
	static value class boxedInt extends x10.lang.Object {
		int val = 0;
	}
}

