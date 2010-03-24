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
 * @author bdlucas
 */

public class FuturePropertyAccessRev extends x10Test {

    class C[S] {
        property p:int = 0;
        val x:S;
        var y:S;
        def foo() {}
        def foo(x:S) {}
        final def foo[T](x:T) {}
        def this(s:S) {
            x = s;
            y = s;
        }
    }

    val c = (future (here.next()) new C[String]("a")).force();

    public def run01(): boolean = {
        val p = Place.places(1);
    	val cc = this.c;
        val f = future (p) {
            	// A property access, is place-safe.
                val a = cc.p;
            return true;
        };
        return f.force();
    }

   

    public def run(): boolean {
    	if (Place.MAX_PLACES == 1) {
    		x10.io.Console.OUT.println("not enough places to run this test");
    		return false;
    	}
    	return run01();
	}

    public static def main(Rail[String]) {
        new FuturePropertyAccessRev().execute();
    }
}
