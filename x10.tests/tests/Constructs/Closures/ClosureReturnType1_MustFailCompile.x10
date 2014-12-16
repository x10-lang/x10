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
 * As with methods, a closure with return type void cannot have a
 * terminating expression.
 *
 * @author bdlucas 8/2008
 */

public class ClosureReturnType1_MustFailCompile extends x10Test {

    def foo() = {}

    public def run(): boolean = {
        val f = ():void => {foo();
            1}; // ERR ShouldNotBeERR (Cannot return a value from closure ()null => void.)
        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureReturnType1_MustFailCompile().execute();
    }
}
