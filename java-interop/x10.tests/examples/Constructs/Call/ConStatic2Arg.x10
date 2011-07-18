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
 * Check that a cast is created for an instance call with a simple clause.
 * @author vj
 */
public class ConStatic2Arg extends x10Test {
	static class A(i:Int) {}
	
	static def m(q:A{self.i==2},  i:Int(q.i)) {
	}
	static def n(i:Int) {
		val a = new A(i);
		// This call will compile only if -strictCalls is not set.
		m(a, i); // ERR
	}
	
	public def run(): boolean {
		try {
			n(3);
			return false;
		} catch (ClassCastException) {
			return true;
		}
	}

	public static def main(Array[String](1)) {
		new ConStatic2Arg().execute();
	}


}
