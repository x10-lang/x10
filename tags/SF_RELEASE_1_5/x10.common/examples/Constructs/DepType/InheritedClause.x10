/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
 /**  Check that the class invariant is implicitly made available to all
 deptypes defined on the class. Here, every type on A implicitly carries
 the clause (:self.i==2 && self.b==2), hence implies (:self.i==self.b).
 
 *@author vj,10/30/2006
 *
 */

import harness.x10Test;

 public class InheritedClause extends x10Test { 
	 
	 class A(int(:self==2) i, int(:self==2) b) {
		 public A(final int(:self==2) i, int(:self==2) b) {
			 property(i, b);
		 }
	 }
	 public boolean  run() { 
		 A a = new A(2,2);
		 A(:i==b) x  = a;
		 return true;
	 }
    
	 public static void main(String[] args) {
		 new InheritedClause().execute();
	 }
   
 }