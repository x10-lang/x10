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
 * @author yoav
 */

public class XTENLANG_1473 extends x10Test {

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_1473().execute();
    }
	
	static class Super {
		val x = 4;
	}
	static class Sub extends Super {
		val y = x; 
		val z = super.x;
		val w = this.x;
	}

	static class BarSuper {
		val i = 4;
	}
	static class Bar extends BarSuper {
		class Super {
			val x = 4;
		}
		class Sub extends Super {
			val y = x; 
			val z = super.x;
			val w = this.x;
			val i1 = Bar.super.i;
			val i2 = Bar.this.i;
		}
	}
}