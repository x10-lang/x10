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

import harness.x10Test;


/**
 * Methods and closures may return values using a return statement. If
 * the method's return type is explicitly declared void, the method may
 * return without a value; otherwise, it must return a value of the
 * appropriate type.
 *
 * @author bdlucas 8/2008
 */

public class ClosureReturnType3_MustFailCompile extends x10Test {

    def foo() = {}

    public def run(): boolean = {
        val f = ():int => {foo();
            return "not an int";}; // ERR
        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureReturnType3_MustFailCompile().execute();
    }
}
