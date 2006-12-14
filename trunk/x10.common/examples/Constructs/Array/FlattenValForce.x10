/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;


/**
 * Test for array reference flattening. Check that flattening works inside a Future.
 */
public class FlattenValForce extends x10Test {
   
    static future<int> rd(final future<int>[.] e, final int i) {
       future<double> fd = future { 3.0 };
       double x = fd.force();
		return
			future { e[i].force() };
	}
   
	public boolean run() {
	  return true;
	}

	public static void main(String[] args) {
		new FlattenValForce().execute();
	}
	
}



