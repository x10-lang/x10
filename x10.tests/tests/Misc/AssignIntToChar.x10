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
 * Assigning a char literal to a char array.
 */
public class AssignIntToChar extends x10Test {

	/**
	 * Testing comments for run
	 */
	public def run(): boolean = {
	    val a = new Rail[char](4, 0 as char);
	    var bit1: boolean = true;
	    var bit2: boolean = false;
	    a(1) = (bit2 ? 'A' : 'C') ;
	    return true;
	}

	/**
	 * Testing comments for main
	 */
	public static def main(var args: Rail[String]): void = {
		new AssignIntToChar().execute();
	}
}
