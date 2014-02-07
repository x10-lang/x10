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
 * Test that a guarded function can be called with args that satisfy the guard.
 *
 * @author vj 10/2009
 */
public class GuardedFunctionInvocation_MustFailCompile extends x10Test {

    def m(x:Int){x==3n} = x;
    def test() {
        m(3n);
        m(4n); // ERR
        f(3n);
        f(4n); // ERR
    }
    val f = (x:Int){x==3n}=>x;

    public def run() = true;

    public static def main(Rail[String]) {
        new GuardedFunctionInvocation_MustFailCompile().execute();
    }
}
