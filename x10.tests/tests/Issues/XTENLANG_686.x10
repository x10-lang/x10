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

import harness.x10Test;

/**

 */

class XTENLANG_686 extends x10Test {

	static class A(a:Long) {
		  def this() : A{1==this.a}{property(1);} // ok
		  //def this(a:Long{self==this.a}) {property(a);} // Semantic Error: This or super cannot be used (implicitly or explicitly) in a constructor formal type.	 Formals: [val a: x10.lang.Long{self==FordesemiFoo#this.a}]
		}
	


	public def run(): boolean {
		new A();
		//new A(1);
		return true;
	}

    public static def main(Rail[String]) {
        new XTENLANG_686().execute();
    }
}
