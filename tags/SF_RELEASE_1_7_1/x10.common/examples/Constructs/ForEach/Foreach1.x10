/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Test for foreach.
 * @author kemal, 12/2004
 */
public class Foreach1 extends x10Test {

	const int N = 100;
	int nActivities = 0;

	public boolean run() {
		final place P0 = here; // save current place
		final dist d = [0:N-1]->here;
		final boolean[.] hasbug = new boolean[d];

		finish foreach (point [i]: d) {
			// Ensure each activity spawned by foreach
			// runs at P0
			// and that the hasbug array was
			// all false initially
			hasbug[i] |= !(P0 == d[i] && here == P0);
			atomic this.nActivities++;
		}
		return !hasbug.reduce(booleanArray.or, false) &&
			nActivities == N;
	}
	public static void main(String[] args) {
		new Foreach1().execute();
	}
}

