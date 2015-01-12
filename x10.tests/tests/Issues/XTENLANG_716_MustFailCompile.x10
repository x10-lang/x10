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

 */

class XTENLANG_716_MustFailCompile extends x10Test {

	class A (x:Int) {
		def this(x:Int){property(x);}
	}

		class B extends A{self.x==2n} {
			def this(x:Int){super(x);} // ERR: Semantic Error: The super(..) call cannot establish the supertype
		}
	



	public def run(): boolean {
		new B(1n);
		
		return true;
	}

    public static def main(Rail[String]) {
        new XTENLANG_716_MustFailCompile().execute();
    }
}
