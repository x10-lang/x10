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

//OPTIONS: -STATIC_CHECKS

import harness.x10Test;

/**
 * Testing that remote field access (for this) is detected by the compiler.
 * 
 * @author vj
 */
public class At_MustFailCompile extends x10Test {
    private val root = GlobalRef[At_MustFailCompile](this);
    var x:Int = 0n;
    def globalRefCanBeUsedOnlyWithObjects(
        GlobalRef[Int] // ERR
    ) {}
    def m(b: GlobalRef[Any]):Int {
        return at (b) {
            // We dont know that this local. 
            root().x // ERR
        }; 
    }

    public def run()=true;

    public static def main(Rail[String]) {
        new At_MustFailCompile().execute();
    }
}
