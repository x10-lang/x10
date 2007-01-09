/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Test the shorthand syntax for an array initializer.
 */
public class IntArrayInitializerShorthand extends x10Test {

	public boolean run() {
		dist d = [1:10, 1:10]->here;
		int[.] ia = new int[d] (point [i,j]) { return i+j; };

		for (point p[i,j]: [1:10,1:10]) chk(ia[p] == i+j);

		return true;
	}

	public static void main(String[] args) {
		new IntArrayInitializerShorthand().execute();
	}
}

