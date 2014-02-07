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
 * Check that a cast is created for an instance call with a simple clause.
 * @author vj
 */
public class ConConstructor2Arg_2_DYNAMIC_CHECKS extends x10Test {
	static class A(i:Int) {}
	def this() {}
	def this(q:A{self.i==2n}, i:Int(q.i)){}
	def this(i:Int) {
		// This call will compile only if -STATIC_CHECKS is not set.
		this(new A(i+1n),i+1n); // ERR warning: generated dynamic check
	}
	
	public def run(): boolean {
		try {
			val x = new ConConstructor2Arg_2_DYNAMIC_CHECKS(2n);
			return false;
		} catch (ClassCastException) {
			return true;
		}
	}

	public static def main(Rail[String]) {
		new ConConstructor2Arg_2_DYNAMIC_CHECKS().execute();
	}


}
