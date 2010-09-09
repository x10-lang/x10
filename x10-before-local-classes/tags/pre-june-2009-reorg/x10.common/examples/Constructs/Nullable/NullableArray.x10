/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Testing arrays that are nullable
 * (causes exception as of 3/12/05).
 *
 * @author kemal 3/2005
 */
public class NullableArray extends x10Test {

	public boolean run() {
		int[] ia1 = new int[2];
		if (ia1[1] != 0) return false;
		nullable<int[]> ia2 = null;
		if (ia2 != null) return false;
		nullable<int[.]> ia3 = null;
		if (ia3 != null) return false;
		ia3 = new int[[0:2]->here];
		if (ia3[2] != 0) return false;
		mycomplex[.] ia4 = new mycomplex[[0:2]->here]
			(point [i]) { return new mycomplex(); };
		if (ia4[2].im != 0) return false;
		nullable<mycomplex[.]> ia5 = new mycomplex[[0:2]->here]
			(point [i]) { return new mycomplex(); };
		if (ia5[2].im != 0) return false;
		nullable<mycomplex[.]> ia7 = null;
		if (ia7 != null) return false;
		return true;
	}

	public static void main(String[] args) {
		new NullableArray().execute();
	}

	static class mycomplex  {
		int re;
		int im;
	}
}

