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

//OPTIONS: -STATIC_CHECKS

import harness.x10Test;

/**
 * Updated to 2.1.
 * 
 * Cannot reference a banged local variable across a place-shift.
 *
 * @author vj
 */
class BangAcrossPlaces_MustFailCompile extends x10Test {
    class C {
        var x:Int=0n;
        def x() =x;
    }
    def m() {
        val x = GlobalRef[C](new C()); // implicitly banged.
        val z = x();
        at (Place.places().next(here)) {
            // this should generate an error.
            val y = x(); // ERR
        }
    }
    public def run() = true;

    public static def main(Rail[String]) {
        new BangAcrossPlaces_MustFailCompile().execute();
    }
}
