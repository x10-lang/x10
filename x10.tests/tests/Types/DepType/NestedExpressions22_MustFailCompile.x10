/*

 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */


/**
* The typechecker must complain when an attempt is made to pass an && expression
* into a type definition call.
 */
	
public class NestedExpressions22_MustFailCompile {
	class C(a:boolean) {
		static type C(b:boolean) = C{self.a==b};
		def m(y:C, x:C) {
		   val z : C(y.a&&x.a)=null; // ERR: IllegalConstraint
		}
	
	}
   
}
