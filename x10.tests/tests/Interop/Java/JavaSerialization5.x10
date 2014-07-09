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
import x10.interop.Java;

// MANAGED_X10_ONLY

public class JavaSerialization5 extends x10Test {

    static class C {
        val a:Any;
        def this() {
            val ja = Java.newArray[Any](1n);
            ja(0n) = "abc";
            a = ja;
        }
    }

    static def test():void {
        val c = new C();
        at (Place.places().next(here)) {
            c.toString();
        }
    }

    public def run(): Boolean = {
        test();
        return true;
    }

    public static def main(args: Rail[String]) {
        new JavaSerialization5().execute();
    }

}
