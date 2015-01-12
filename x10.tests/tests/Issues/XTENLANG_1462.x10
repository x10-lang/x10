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
 * @author yoav
 */

public class XTENLANG_1462 extends x10Test {

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_1462().execute();
    }
	
	
	static class Bar(p:Long) {
		def this(q:Long):Bar{self.p==q} {
			property(q);
		}
		static def test(z:Bar{p==3}) {
			val b:Bar{p==3} = new Bar(3);
		}
		def membertest(z:Bar{self.p==3}) {p==2} {
			val b:Bar{self.p==3} = new Bar(3);
		}
	}

}