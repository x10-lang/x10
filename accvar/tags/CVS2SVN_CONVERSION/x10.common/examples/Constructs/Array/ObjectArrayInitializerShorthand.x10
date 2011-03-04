/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Test the shorthand syntax for object array initializer.
 *
 * @author igor, 12/2005
 */
public class ObjectArrayInitializerShorthand extends x10Test {

	public boolean run() {
		final dist d = [1:10,1:10]->here;
		final dist[.] ia = new dist[d] (point [i,j]) { return d; };
		for (point [i,j]: ia) chk(ia[i,j] == d);
		return true;
	}

	public static void main(String[] args) {
		new ObjectArrayInitializerShorthand().execute();
	}
}

