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

public class AsyncInit2 extends x10Test {
	static def test1():Boolean {
		var v1:Long = 1;
		finish async {
			v1 = 2;
			finish async {
				v1 = 3;
			}
		}
		chk(v1 == 3);
		return true;
	}
	static def test2():Boolean {
		var v1:Long = 1;
		finish async {
			// v1 = 2;
			finish async {
				v1 = 3;
			}
		}
		chk(v1 == 3);
		return true;
	}
    public def run(): Boolean {
    	test1();
    	test2();
        return true;
    }
    public static def main(Rail[String]) {
        new AsyncInit2().execute();
    }
}
