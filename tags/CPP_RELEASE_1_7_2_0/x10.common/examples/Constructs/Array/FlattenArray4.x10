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
public class FlattenArray4 extends x10Test {
    int[.] a;
    public FlattenArray4() {
      a = new int[[1:10,1:10]] (point [i,j]) { return i+j;};
    }
    int m(int x) {
      return x;
    }
	public boolean run() {
	    int x = m(a[1,1]); // being called in a method to force flattening.
	    int y = m(a[2,2]);
	    return x+y==1+1+2+2;
	}

	public static void main(String[] args) {
		new FlattenArray4().execute();
	}
	
}

