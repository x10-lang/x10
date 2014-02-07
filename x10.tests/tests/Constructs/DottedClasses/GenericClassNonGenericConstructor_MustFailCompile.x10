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


/*
Must require that a 
*/

import x10.util.*;
import harness.x10Test;

public class GenericClassNonGenericConstructor_MustFailCompile extends x10Test {
	// This generic class has no explicit constructor.
	// Therefore the constructor def this(){} will be automatically
	// constructed. However, the return type of this constructor says nothing about
	// the type parameter. So what is the type of new A()? 
	// Cant be A -- need a type parameter.
	// Cant be A[Any]
	static class A[T] {
		public def toString() = "" + hashCode();
	}
	def m() {
		val a = new A(); // ERR
		
		Console.OUT.println("a = " + a);
		// val b = a as A[Int];
		// Console.OUT.println("a as A[Int]=" + b);
	}
 
  public def run()={ m(); true}
  public static def main(Rail[String]) {
      new GenericClassNonGenericConstructor_MustFailCompile().execute();
  }
}
