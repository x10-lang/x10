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

import harness.x10Test;

/**

 */

class XTENLANG_692_MustFailCompile extends x10Test {

	// In general, variables (i.e., local variables, parameters, properties, fields) are visi- 
	// ble at T if they are defined before T in the program. 

	//fail something about n being used before it's declared.

	class A (x:Int) {
	  def backwards(p: A{p.x==  // ERR
	        n}, // ERR: Could not find n  ShouldNotBeERR (Could not find field or local variable "n".)
	    n:Int) = 1n;
	  def this(){property(1n);}
	}
	


	public def run(): boolean {
		new A();
		
		return true;
	}

    public static def main(Rail[String]) {
        new XTENLANG_692_MustFailCompile().execute();
    }
}
