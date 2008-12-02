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
  
  To check that this test does what it was intended to, examine
  the output Java file. It should have a series of local variables
  pulling out the subters of m(a[1,1]).
 */
 
public class FlattenFutureCall extends x10Test {
    int[.] a;
    public FlattenFutureCall() {
      a = new int[[1:10,1:10]] (point [i,j]) { return i+j;};
    }
    
	public boolean run() {
	 boolean x = future(a.distribution[1,1]){ true}.force();
	    return x;
	}

	public static void main(String[] args) {
		new FlattenFutureCall().execute();
	}
	
}
