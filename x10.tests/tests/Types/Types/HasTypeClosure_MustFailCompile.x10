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
 * Check a local variable can have a has type.
 *
 * @author vj
 */
public class HasTypeClosure_MustFailCompile extends x10Test {

    def m(x:Long{self==1}) = x;

    public def run(): boolean = {
        val x = (y:Long)<: Long => y;
        val z <:Long(0) = x(1); // ERR
        return true;
    }

    public static def main(Rail[String])  {
        new HasTypeClosure_MustFailCompile().execute();
    }
}
