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
 * Test that a guarded function can be called with args that satisfy the guard.
 *
 * @author vj 10/2009
 */
public class GuardedFunctionInvocation_MustFailCompile extends x10Test {

	val f = (x:Int){x==3}=>x;
	
	public def run() = f(4)==3;

	public static def main(Rail[String]) {
		new GuardedFunctionInvocation_MustFailCompile().execute();
	}
}
