/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

import harness.x10Test;

/**
 * Check that a cast is created for an instance call for a struct. Method arg's type references
 * another arg.
 * @author vj
 */
public class ConStructInstance2Arg_2 extends x10Test {
	static struct A(i:Int) {

		def m(q:A{self.i==2},  i:Int(q.i)) {
		}
		def n(i:Int) {
			val a = A(i);
			// This call will compile only if -strictCalls is not set.
			m(a, i+1); // ERR
		}
	}
	public def run(): boolean {
		try {
			A(1).n(2);
			return false;
		} catch (ClassCastException) {
			return true;
		}
	}

	public static def main(Rail[String]) {
		new ConStructInstance2Arg_2().execute();
	}


}
