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
 * Testing that an at(b) d.x is flagged as illegal.
 * Since it is not known statically that b and d have the same home,
 * doing an at (b.root) should not enable access to d.
 * @author vj
 */
public class AtCheck3a_MustFailCompile extends x10Test {
    class T {
        private val root = GlobalRef[T](this);
        var x:int=0n;
        def m(b: T, d:T) {
            at (b.root) {
                val e = d.root().x;  // ERR: No valid method call found for call in given type.
            }
        }
    }
    
    public def run() = true;

    public static def main(Rail[String]) {
        new AtCheck3a_MustFailCompile().execute();
    }
}
