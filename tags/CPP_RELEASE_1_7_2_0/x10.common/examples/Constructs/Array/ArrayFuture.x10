/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Testing arrays of future<T>.
 *
 * @author kemal, 5/2005
 */
public class ArrayFuture extends x10Test {

	public boolean run() {
		final dist d = [1:10,1:10]->here;
		final future<int>[.] ia = new future<int>[d] (point [i,j]) { return future(here){i+j}; };
		for (point [i,j]: ia) chk(ia[i,j].force() == i+j);
		return true;
	}

	public static void main(String[] args) {
		new ArrayFuture().execute();
	}
}

