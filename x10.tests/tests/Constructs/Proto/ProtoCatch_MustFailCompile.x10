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
 * Throw/Catch types cannot be proto types.
 * @author vj
 */
public class ProtoCatch_MustFailCompile extends x10Test {

	value A extends Throwable {}
    
	def m() throws A {
		
	}
    public def run() {
    	try {
    		m();
    	} catch (z: proto A) {
    		
    	}
    }

    public static def main(Rail[String])  {
	new ProtoCatch_MustFailCompile().execute();
    }
}
