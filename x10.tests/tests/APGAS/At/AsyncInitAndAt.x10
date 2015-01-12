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
 * If an async initialized val is referenced 
 * within the body of a subsequent at, it should
 * be copied (just like any other val referneced by an at).
 *
 */
public class AsyncInitAndAt extends x10Test {
    static class C {
      var data:long;
      def this(d:long) { data = d; }
    }

    public def run():boolean {
	return doit(5);
    }
 
    public def doit(val x:long):boolean {
        val y:C;
        finish {
            async { y = new C(x + 5); }
        }
	at (Place.places().next(here)) {
            chk(y.data == 10); // can access copied async initialized val
            y.data = 20; // mutate copy
        }
	return y.data == 10; // original unchanged
    }

    public static def main(Rail[String]) {
        new AsyncInitAndAt().execute();
    }
}
