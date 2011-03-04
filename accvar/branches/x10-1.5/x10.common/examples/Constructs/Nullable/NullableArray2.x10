/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Testing arrays whose elements are possibly nullable.
 *
 * @author kemal 4/2005
 */
public class NullableArray2 extends x10Test {

	public boolean run() {
		nullable<int>[] ia1 = new nullable<int>[2];
		if (ia1[1] != null) return false;

		nullable<nullable<int>[]> ia2 = null;
		if (ia2 != null) return false;

		nullable<nullable<int>[.]> ia3 = null;
		if (ia3 != null) return false;

		ia3 = new nullable<int>[[0:2]->here];
		if (ia3[2] != null) return false;

		// it is forbidden to assign an int [.] to a nullable<int>[.]
		// ia3 = new int[[0:2]->here];

		// cannot assign a nullable int y to an element
		// of an array of int's, unless y is not null
		nullable<int> y = null;
		boolean gotException = false;

		nullable<mycomplex>[.] ia4 =
			new nullable<mycomplex>[[0:2]->here]
			(point [i]) { return new mycomplex(); };
		if (ia4[2].im != 0) return false;

		nullable<nullable<mycomplex>[.]> ia5 =
			new nullable<mycomplex>[[0:2]->here]
			(point [i]) { return new mycomplex(); };
		if (ia5[2].im != 0) return false;

		nullable<nullable<mycomplex>[.]> ia7 = null;
		if (ia7 != null) return false;

		nullable<nullable<mycomplex>[.]> ia9 =
			new nullable<mycomplex>[[0:2]->here];
		if (ia9[2] != null) return false;

		return true;
	}

	public static void main(String[] args) {
		new NullableArray2().execute();
	}

	static class mycomplex {
		int re;
		int im;
	}

	static class X {
		static boolean trueFun() { return true; }
	}
}

