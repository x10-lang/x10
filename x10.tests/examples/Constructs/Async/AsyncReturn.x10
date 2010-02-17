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
 * Testing returns in an async body.
 *
 * @author vj
 */
public class AsyncReturn extends x10Test {

	public def run(): boolean = {
		class T {
			var t: int;
		}
		val f: T! = new T();
		f.t = 1;
		val v: int = f.t;
		val body = ()=> {
			if (v == 1)
			return;
		    async at (f) {
			   atomic {
				  f.t = 2;
			   }
		     }
		};
		finish async body();
		return (f.t == 1);
	}

	public static def main(var args: Rail[String]): void = {
		new AsyncReturn().execute();
	}
}
