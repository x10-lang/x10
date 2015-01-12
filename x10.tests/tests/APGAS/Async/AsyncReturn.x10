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
 * Testing returns in an async body.
 *
 * @author vj
 */
public class AsyncReturn extends x10Test {

	public def run(): boolean = {
		class T {
			private val root = GlobalRef[T](this);
			transient var t: int;
		}
		val f = new T();
		val froot = f.root;
		f.t = 1n;
		val v: int = f.t;
		val body = ()=> {
			if (v == 1n)
			return;
		    async at (froot) {
			   atomic {
				  froot().t = 2n;
			   }
		     }
		};
		finish async body();
		return (f.t == 1n);
	}

	public static def main(Rail[String]) {
		new AsyncReturn().execute();
	}
}
