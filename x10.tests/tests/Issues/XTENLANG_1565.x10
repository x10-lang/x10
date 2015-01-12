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
import x10.compiler.NonEscaping;

/**
 * @author yoav
 */

class XTENLANG_1565 extends x10Test {

	val m:Long;
	val n:Long;
	val q:Long;
	val z:Long;
	def this() {
		finish {
			async { z = 0; }
		}
		
		var i:Long;
		var j:Long;
		var k:Long;

		i=1;
		// i:[1,1,1,1]
		finish {
			m=2;
			// m:[1,1,1,1]
			if (true) {
				async { 
					n=3; i=4; j=5; k=6; q=7;
					// n:[1,1,1,1] i:[2,2,2,2] j:[1,1,1,1] k:[1,1,1,1] q:[1,1,1,1]
					f(m); f(i); f(n); f(q);
				}
				// n:[0,0,1,1] i:[1,1,2,2] j:[0,0,1,1] k:[0,0,1,1] q:[0,0,1,1]
				k=8;
				// k:[1,1,2,2]
				f(k);
			} else {
				// n:[0,0,0,0] i:[1,1,1,1] j:[0,0,0,0] k:[0,0,0,0]
				n=9; q = 999;
				f(n); f(m); f(i);
				// n:[1,1,1,1]
			}
			// k:[0,1,0,2] n:[0,1,1,1] i:[1,1,1,2] j:[0,0,0,1] q:[0,0,0,1]
			f(m); f(i); 
			k=11;
			// k:[1,2,1,3]
			f(k);
		}
		// k:[1,3,1,3] n:[1,1,1,1] i:[1,2,1,2] j:[0,1,0,1] q:[0,1,0,1]
		f(k); f(n); f(m); f(i);
		f(q);
		j=12;
		// j:[1,2,1,2]
		f(j);
	}
	@NonEscaping final def f(i:Long):Long=i+1;

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_1565().execute();
    }
}
