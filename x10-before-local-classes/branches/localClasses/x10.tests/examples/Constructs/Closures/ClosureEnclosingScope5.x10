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
 * The closure body may refer to instances of enclosing classes using the
 * syntax C.this, where C is the name of the enclosing class.
 *
 * @author bdlucas 8/2008
 */

public class ClosureEnclosingScope5 extends ClosureTest {

	val a = 1;

	class C(a:int) {
		def this(x:int) { 
			property(x);
		}
		class D(a:int) {
			def this(x:int) { 
				property(x);
			}
			def sum() {
				val a1 = ClosureEnclosingScope5.this.a;
				val a2 = C.this.a;
				val a3 = D.this.a;
				return a1 + a2 + a3 + a;
			}
		}
	}

	public def run(): boolean = {

			check("new C(2).new D(4).sum", new C(2).new D(4).sum(), 11);

			return result;
	}

	public static def main(Rail[String])  {
		new ClosureEnclosingScope5().execute();
	}
}
