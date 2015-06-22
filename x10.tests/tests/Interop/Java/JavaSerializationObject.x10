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
import x10.compiler.Native;

// MANAGED_X10_ONLY

public class JavaSerializationObject extends x10Test {

    @Native("java", "new Object()")
    public static def new_Object() : Any = null;

    def testObject() {
        val obj = new_Object();
        at (here) {
            val copy = obj;
            chk(copy.typeName().equals("x10.lang.Any"));
            // Success if no exception occurs, see XTENLANG-3018
        }
        return true;
    }

    public def run(): Boolean {
        return testObject();
    }

    public static def main(args: Rail[String]) {
        new JavaSerializationObject().execute();
    }
}
