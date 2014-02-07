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

// SKIP_MANAGED_X10 : XTENLANG-3003 Implementation limitation in Managed X10 

public class JavaArray2_MustFailCompile extends x10Test {

    public def run(): Boolean {
        val jaui = Java.newArray[UInt](10n);
        val ijaui = jaui instanceof Java.array[UInt];
        return ijaui;
    }

    public static def main(args: Rail[String]) {
        new JavaArray2_MustFailCompile().execute();
    }

}
