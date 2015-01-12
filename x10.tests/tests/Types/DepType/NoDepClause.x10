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
 * Check that a non-dependent type String is equivalent to String(:)
 *
 * @author igor 6/17/2007
 * @author nystrom 6/17/2007
 */
public class NoDepClause extends x10Test {
	public def run(): boolean = {
	    val r:String = "abcd";
	    return r != "a";
	}
	public static def main(val args: Rail[String]): void = {
	    new NoDepClause().execute();
	}
}
