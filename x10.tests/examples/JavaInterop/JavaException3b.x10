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
import x10.interop.java.Throws;

// MANAGED_X10_ONLY

public class JavaException3b extends x10Test {
    def this():JavaException3b @Throws[java.lang.Throwable] {
        throw new java.lang.Throwable("I like Java.");          
    }

    public def run(): Boolean = true;

    public static def main(args: Array[String](1)) {
        var pass: Boolean = false;
        try {
            new JavaException3b().execute();
        } catch (e:java.lang.Throwable) {
            pass = true;
            //e.printStackTrace();
        }
        chk(pass);
    }
}
