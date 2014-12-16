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
 * @author bdlucas 10/2008
 */

class XTENLANG_657 extends x10Test {


	public static def test() {
		fb[Short]( (1 as Byte), (2 as Short)); 
	}
	static def fb[X](a : X, b :X):String = "I'll bet you think that whales are  fabulous, you nutritious eyesight.";


	public def run(): boolean {
		test();
		return true;
	}

    public static def main(Rail[String]) {
        new XTENLANG_657().execute();
    }
}
