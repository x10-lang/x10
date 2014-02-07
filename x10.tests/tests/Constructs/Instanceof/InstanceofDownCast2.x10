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
 * Purpose: Checks "side effect aware" checking code detects when a constraint is not meet.
 * Issue: The cast is not valid til constraints is not meet.
 * X10DepTypeSubClassOneB(:p==2&&a==2) <-- X10DepTypeSubClassOneB(:p==1&&a==1)
 * @author vcave
 **/
public class InstanceofDownCast2 extends x10Test {
	public def run(): boolean = {
		return !(this.getX10DepTypeSubClassOneB(1,1) 
		instanceof X10DepTypeSubClassOneB{p==2&&a==2});
	}

	private def getX10DepTypeSubClassOneB(var c1: long, var c2: long): Any = {
		return new X10DepTypeSubClassOneB(c1, c2);
	}

	public static def main(var args: Rail[String]): void = {
		new InstanceofDownCast2().execute();
	}
}
