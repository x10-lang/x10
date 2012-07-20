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
import x10.compiler.Native;

// MANAGED_X10_ONLY

public class JavaException6 extends x10Test {

    public abstract static class Base {
    	@Native("java", "null")
        public native def f(): Any throws java.io.IOException;
    }

    public static class Derived extends Base {
        public def f(): Any throws java.io.IOException {
        	return super.f();
        }
    }

    public def run(): Boolean = true;

    public static def main(args: Array[String](1)) {
        new JavaException6().execute();
    }

}
