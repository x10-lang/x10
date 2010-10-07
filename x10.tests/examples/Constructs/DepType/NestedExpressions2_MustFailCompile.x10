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
//OPTIONS: -STATIC_CALLS

/**

Check that and(x,y) of type C(x.a&&y.a) cannot be assigned to
a variable of type C(y.a&&x.a), under static calls.

(This may change later.)
 */

	
public class NestedExpressions2_MustFailCompile extends x10Test {
	class C(a:boolean) {
		static type C(b:boolean) = C{self.a==b};
		def this(u:boolean):C(u) { property(u);}
		def and(x:C, y:C): C(x.a && y.a) = new C(x.a&&y.a);
		def n() {
			val x = new C(true);
			val y = new C(true);
			val z: C{self.a==(y.a&&x.a)} = and(x,y);
		}
	}
    public def run() = true;

    public static def main(Array[String](1))  {
        new NestedExpressions2_MustFailCompile().execute();
    }
}
