/**
 * A test for bad place exceptions
 *
 * @author Christoph von Praun
 */
public class Exceptions_BadPlace {
	const int M = 10;
	public boolean run() {
		if (place.MAX_PLACES == 1) return true;

		boolean gotException;

		final boxedBoolean[.] B = new boxedBoolean[dist.factory.block([0:M-1])]
			(point [i]) { return new boxedBoolean(); };
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
			for(point [i]: VA) { x += VA[i]; }
		} catch (BadPlaceException e) {
			gotException = true;
		}
		System.out.println("4");
		if (gotException) return false;

		return true;
	}

	public static void main(String[] args) {
		final boxedBoolean b=new boxedBoolean();
		try {
			finish async b.val=(new Exceptions_BadPlace()).run();
		} catch (Throwable e) {
			e.printStackTrace();
			b.val=false;
		}
		System.out.println("++++++ "+(b.val?"Test succeeded.":"Test failed."));
		x10.lang.Runtime.setExitCode(b.val?0:1);
	}
	static class boxedBoolean {
		boolean val = false;
	}
	// CVP -- if this class is declared as value class, then case 3 succeeds.
	static value class boxedInt {
		int val = 0;
	}
}

