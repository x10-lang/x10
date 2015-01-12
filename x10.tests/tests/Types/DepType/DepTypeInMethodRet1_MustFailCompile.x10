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
 * Check that a method can have a deptype argument and it is checked properly.
 * Return depclause cannot reference a non-final parameter.
 *
 * @author vj
 */
public class DepTypeInMethodRet1_MustFailCompile extends x10Test {
   
    public def m(var t: boolean): boolean(t)= t; // ERR: A var local variable is not allowed in a constraint.
	public def run() = m(true);

	public static def main(Rail[String]) {
		new DepTypeInMethodRet1_MustFailCompile().execute();
	}
}
