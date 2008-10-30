/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;


/**
 * Test for array reference flattening. 
 */
public class Force extends x10Test {
   
    static int rd(final future<int> e, final int i, final int j) {
      final int x = e.force();
		return
			future { x }.force();
	}
   
	public boolean run() {
	  return true;
	}

	public static void main(String[] args) {
		new Force().execute();
	}
	
}



