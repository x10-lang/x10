/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2012.
 */

import harness.x10Test;
import x10.interop.Java;

// MANAGED_X10_ONLY

public class JavaSerialization5 extends x10Test {

    static class C {
        val a:Any;
        def this() {
            val ja = Java.newArray[Any](1);
            ja(0) = "abc";
            a = ja;
        }
    }

    static def test():void {
        val c = new C();
        at (here.next()) {
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
