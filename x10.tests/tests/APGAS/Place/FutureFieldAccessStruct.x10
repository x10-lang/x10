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
 * A field of a struct can be accessed anywhere.
 * @author vj
 * @author bdlucas
 */
import x10.util.concurrent.Future;
public class FutureFieldAccessStruct extends x10Test {

   static struct C[S] {
        property p():int = 0n;
        val x:S;
        def foo() {}
        def foo(x:S) {}
        final def foo[T](x:T) {}
        def this(s:S) {
            x = s;
        }
    }

    val c = C[String]("1");

    public def run02(): boolean = {
    		val p = Place(1);
    		val cc = this.c;
            val f = Future.make[boolean](() => at(p) {
            	// cannot access a field that is not global
                val a = cc.x;
            return true;
        });
        return f.force();
    }

    public def run(): boolean {
    	if (Place.numPlaces() == 1L) {
    		x10.io.Console.OUT.println("not enough places to run this test");
    		return false;
    	}
    	return run02();
	}

    public static def main(Rail[String]) {
        new FutureFieldAccessStruct().execute();
    }
}
