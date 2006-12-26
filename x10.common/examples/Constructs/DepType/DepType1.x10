/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
/** Tests many syntactic features of dep types.
 *@author pvarma
 *
 */

import harness.x10Test;

public class DepType1(int i, int j) extends x10Test { 

	int (:v == 0) v;
	boolean (:b == true) b;
    
     //  property declaration for an inner class.
    class Test(int k) extends DepType1 { 
        Test(int kk) {
            super(kk,kk);
	    property(kk);
        }
    }
    
    Test (:k == 3) t; 
    nullable < Test> (:self == null) n;
    
    
    public DepType1(int i, int j) {
      this.i = i;
      this.j = j;
    }
    
 
    public  boolean run() {
	DepType1(:i==3) d = (DepType1(:i==3)) new DepType1(3,6); 
	return true;
    }
	
    public static void main(String[] args) {
        new DepType1(3,9).execute();
    }
   

		
}
