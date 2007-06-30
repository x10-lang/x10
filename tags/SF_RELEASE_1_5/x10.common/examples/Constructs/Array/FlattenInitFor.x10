/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;


/**
 * 
 */
public class FlattenInitFor extends x10Test {
    int[.] a;
    public FlattenInitFor() {
      a = new int[[1:10,1:10]] (point [i,j]) { return i;};
    }
    
    public boolean run() {
        for (int e = future (a.distribution[1,1]) { a[1,1] }.force(); e < 3 ; e++) 
	  System.out.println("done.");		
	 return true;
	}

    public static void main(String[] args) {
	new FlattenInitFor().execute();
    }
	
}

