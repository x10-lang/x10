/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Test the shorthand syntax for array of arrays initializer.
 *
 * @author igor, 12/2005
 */
public class ArrayArrayInitializerShorthand extends x10Test {

	public boolean run() {
		final dist d = [1:10,1:10]->here;
		final int[.] a = new int[d];
		final int[.][.] ia = new int[.][d] (point [i,j]){ return a; };
		for (point [i,j]: ia) chk(ia[i,j] == a);
		return true;
	}

	public static void main(String[] args) {
		new ArrayArrayInitializerShorthand().execute();
	}
}

