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
 * Check that a safe method can be overridden only by a safe method.
 * @author vj  9/2006
 */
public class SafeOverride2_MustFailCompile extends x10Test {

    class T1 {
      public safe def m(): void = { }
    }
    class T2 extends T1 {
      public local nonblocking def m(): void = { /* should give a compile error. */ }
    }
   
	public def run(): boolean = {
		
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new SafeOverride2_MustFailCompile().execute();
	}

	
}
