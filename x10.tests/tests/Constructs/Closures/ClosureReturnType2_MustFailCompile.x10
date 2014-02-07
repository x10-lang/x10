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
import x10.compiler.*; // @Uncounted @NonEscaping @NoThisAccess
import x10.compiler.tests.*; // err markers


/**
 * Methods and closures may return values using a return statement. If
 * the method's return type is expliclty declared void, the method may
 * return without a value; otherwise, it must return a value of the
 * appropriate type.
 *
 * @author bdlucas 8/2008
 */

public class ClosureReturnType2_MustFailCompile extends x10Test {

    def foo() = {}

    public def run(): boolean = {
        @ERR val f = ():int => {foo();};  // Closure must return a value of type x10.lang.Int
        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureReturnType2_MustFailCompile().execute();
    }
}
