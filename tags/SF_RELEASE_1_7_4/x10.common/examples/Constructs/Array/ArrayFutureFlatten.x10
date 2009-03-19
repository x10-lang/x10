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
public class ArrayFutureFlatten extends x10Test {

	public boolean run() { 
		int[.] A = new int[[1:10,1:10]];
		final int[.] B = new int[[1:10,1:10]];
		int b = future { 3 } .force();
		chk(0 == future {B[1,1]}.force());
		return true;
	}
	

	public static void main(String[] args) {
		new ArrayFutureFlatten().execute();
	}
}

