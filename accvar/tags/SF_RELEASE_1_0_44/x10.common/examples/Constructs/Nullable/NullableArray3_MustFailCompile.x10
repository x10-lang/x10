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
 *
 * Comparing non-nullable to null should cause compiler
 * to report an error.
 *
 * @author kemal 4/2005
 */
public class NullableArray3_MustFailCompile extends x10Test {

	public boolean run() {
		int[] ia1 = new int[2];
		if (ia1 == null) return false;
		mycomplex[.] ia2 = new mycomplex[[0:2]->here]
			(point [i]) { return new mycomplex(); };
		if (ia2 == null) return false;
		if (ia2[2] == null) return false;
		return true;
	}

	public static void main(String[] args) {
		new NullableArray3_MustFailCompile().execute();
	}

	class mycomplex {
		int re;
		int im;
	}
}

