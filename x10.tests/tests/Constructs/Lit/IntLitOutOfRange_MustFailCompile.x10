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
 * An error must be thrown by the compiler on encountering a lit with type int and the value
 of the literal cannot be stored in an int. 
 *
 * @author vj 1/2006
 */
public class IntLitOutOfRange_MustFailCompile extends x10Test {

	public def run(): boolean = {
		x10.io.Console.OUT.println(2147493647n); // ERR
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new IntLitOutOfRange_MustFailCompile().execute();
	}

	
}
