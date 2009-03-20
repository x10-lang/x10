/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Array Initializer test.
 */
public class ArrayInitializer extends x10Test {

	public boolean run() {
		region(:rank==1) e = [0:9];
		region r = [e, e, e];
		//TODO: next line causes runtime error
		//dist d=r->here;
		final dist d = [0:9,0:9,0:9]->here;

		final int value [:distribution==d] ia =
			new int value [d]
			(point [i,j,k]) {
				return i;
			};

		for (point [i,j,k]: d) chk(ia[i,j,k] == i);

		return true;
	}

	public static void main(String[] args) {
		new ArrayInitializer().execute();
	}
}

