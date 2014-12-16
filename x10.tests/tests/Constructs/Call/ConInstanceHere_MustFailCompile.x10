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

// OPTIONS: -STATIC_CHECKS

/**
 * Check that a cast is created for an instance call with a simple clause.
  Changed for 2.1. 
 * @author vj
 */
public class ConInstanceHere_MustFailCompile extends x10Test {
	private val root = GlobalRef[ConInstanceHere_MustFailCompile](this);
	def m() {}
	def n() {
		at (Place.places().next(here)) {
		  root().m(); // ERR: Semantic Error: Method or static constructor not found for given call.	 Call: root()
		}
	}
	
	public def run(): boolean {
		try {
			n();
			return false;
		} catch (ClassCastException) {
			return true;
		}
	}

	public static def main(Rail[String]) {
		new ConInstanceHere_MustFailCompile().execute();
	}


}
