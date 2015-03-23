/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

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
	 
	 class A(i:int(2n), b:int(2n)) {
		 public def this(i:int(2n), b:int(2n)):A{
			property(i,b); 
		 }
	 }
	 public def run(): boolean { 
		 var a: A = new A(2n,2n);
		 var x: A{i==3n} = a; // ERR
		 return true;
	 }
    
	 public static def main(var args: Rail[String]): void {
		 new InheritedClause_MustFailCompile().execute();
	 }
   
 }
