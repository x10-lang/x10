/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2011.
 */

import harness.x10Test;

// MANAGED_X10_ONLY

public class JavaException2 extends x10Test {
    def javaException2() throws java.lang.Throwable {
        throw new java.lang.Throwable("I like Java.");
    }

    public def run(): Boolean {
        var pass: Boolean = false;
        try {
            javaException2();
        } catch (e:java.lang.Throwable) {
            pass = true;
            //e.printStackTrace();
        }
        return pass;
    }

    public static def main(args: Array[String](1)) {
        new JavaException2().execute();
    }
}
