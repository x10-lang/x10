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
import x10.interop.Java;

// MANAGED_X10_ONLY

public class JavaClass1 extends x10Test {

    def test() {
        val o = Java.getClass(this);
        val n = o.getName();
    }

    public def run(): Boolean = {
        test();
        return true;
    }

    public static def main(args: Rail[String]) {
        new JavaClass1().execute();
    }
}
