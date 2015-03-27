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
import x10.compiler.Native;

// MANAGED_X10_ONLY

/**
 * The point of this test case is to see if exception stack traces 
 * are being properly serialized.
 */
public class JavaSerialization3 extends x10Test {
        
    static def test():void {
        try {
            (null as String).length();
        } catch (e:NullPointerException) {
            at (Place.places().next(here)) {
	        // Checking for a minimal size is not a complete check,
		// but trying to be too picky about the contents of the
		// stack trace can be fragile in the presence of inlining, etc.
	        val st = e.getStackTrace();
		chk(st.size > 5, "Stack trace too short...where did it go?");
            }
        }
    }

    public def run(): Boolean {
        test();
        return true;
    }

    public static def main(args: Rail[String]) {
        new JavaSerialization3().execute();
    }

}
