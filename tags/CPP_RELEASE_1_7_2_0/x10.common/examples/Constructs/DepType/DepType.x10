/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
/** Tests many syntactic features of dep types.
 *@author vj, 5/17/2006
 *
 */

import harness.x10Test;

public class DepType(int i, int j) extends x10Test { 
    
     //  property declaration for an inner class.
    class Test(int k) extends DepType { 
        Test(int kk) {
            super(3,4);
            property(kk);
        }
    }
    class Test2 extends DepType {
        Test2() {
        super(3,5);
        }
    }
    
     //  thisClause on a class, and extension of a deptyped class
     class Test3  extends DepType { 
        final int k;
        Test3(:j==k)(int v) {
        super(v,v);
        k = v;
        }
    }
     
    // A constructor may specify constraints on properties that are true of the returned object.
    public DepType(:self.i==i &&self.j==j )  (final int i, final int j  ) {
        property(i,j);
    }
    
    //  method specifies a thisClause.
      DepType(:self.i==3&&self.j==3)  make(int(:self==3) i ) { 
       return new DepType(i,i);
       }
   
    // a local variable with a dep clause.
    public  boolean run() {
	DepType(:i==3) d =  new DepType(3,6); 
	return true;
    }
	
    //  a method whose return type is a deptype
   public boolean(:self==true)  run3() { 
        System.out.println("i (=3?) = " + i);
        return true;
    }
    public boolean  run4( int j) {
        System.out.println("i (=3?) = " + i);
        return true;
    }
    public static void main(String[] args) {
        new DepType(3,9).execute();
    }
   

		
}
