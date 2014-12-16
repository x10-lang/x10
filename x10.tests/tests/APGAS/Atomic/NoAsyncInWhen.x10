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
 * A conditional atomic cannot spawn an async. Check that a runtime exception is thrown.
 * @vj
 */
public class NoAsyncInWhen extends x10Test {

	var b: boolean;
	
	public def run(): boolean = {
			b=true;
			try {
		      when (b==true)
		        async 
		          x10.io.Console.OUT.println("Cannot reach this point.");
		      return false;
			} catch (IllegalOperationException) {
				return true;
			}
	}

	public static def main(Rail[String]) {
		new NoAsyncInWhen().execute();
	}
}
