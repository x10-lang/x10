/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;


/**
 * Test for array reference flattening. Checks that after flattening
 the variable x and y can still be referenced, i.e. are not 
 declared within local blocks.
 */
public class FlattenCast extends x10Test {
    int[.] a;
    public FlattenCast() {
      a = new int[[1:10,1:10]] (point [i,j]) { return i+j;};
    }
    int m(int x) {
      return x;
    }
	public boolean run() {
	    double x = (double) m(a[1,1]); // being called in a method to force flattening.
	  return 2==x;
	}

	public static void main(String[] args) {
		new FlattenCast().execute();
	}
	
}

