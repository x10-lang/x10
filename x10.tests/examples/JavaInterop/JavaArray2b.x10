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
import x10.array.*;

// MANAGED_X10_ONLY

public class JavaArray2b extends x10Test {
	
    public def run(): Boolean {
        var ok:Boolean = true;
        
        val a:Any = Java.newArray[Int](10);

        if (!(a instanceof Java.array[Int])) {
            Console.OUT.println("Failed: a instanceof Java.array[Int]");
            ok = false;
        }

        // Implementation limitation in X10 2.3
        if (a instanceof Java.array[UInt]) {
            Console.OUT.println("Failed: !(a instanceof Java.array[UInt]). This is an implementation limitation.");
            ok = false;
        }

        return ok;
    }

    public static def main(args: Rail[String]) {
        new JavaArray2b().execute();
    }

}
