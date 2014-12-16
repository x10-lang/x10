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


/**
 * if the method does not return a value, the inferred type is void
 *
 * @author bdlucas 8/2008
 */

public class ClosureReturnType7_MustFailCompile extends x10Test {

    public def run(): boolean = {
        
        // inferred to be void
        val f = (x:long) => {};

        // should fail because f() is void
        val g = f(0); // ERR

        return true;
    }


    public static def main(var args: Rail[String]): void = {
        new ClosureReturnType7_MustFailCompile().execute();
    }
}
