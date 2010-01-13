/**
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

 public class InheritedClause_MustFailCompile extends x10Test { 
	 
	 class A(i:int(2), b:int(2)) {
		 public def this(i:int(2), b:int(2)):A ={
			property(i,b); 
		 }
	 }
	 public def run(): boolean = { 
		 var a: A = new A(2,2);
		 var x: A{i==3} = a;
		 return true;
	 }
    
	 public static def main(var args: Rail[String]): void = {
		 new InheritedClause_MustFailCompile().execute();
	 }
   
 }
