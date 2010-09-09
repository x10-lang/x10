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
 * A next statement cannot occur in an atomic.
 * @vj
 */
public class NoNextInAtomic_MustFailCompile extends x10Test {

	var b: boolean;
	
	public def run(): boolean = {
	async {
	  var c: Clock = Clock.make();
		atomic {
		  next;
		  }
		 
		  }
		   return true;
	}

	public static def main(var args: Rail[String]): void = {
		new NoNextInAtomic_MustFailCompile().execute();
	}
}
