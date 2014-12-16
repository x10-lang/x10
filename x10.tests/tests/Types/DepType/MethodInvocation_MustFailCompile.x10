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

// If we do not run with STATIC_CHECKS it generates:
// Warning: Generated a dynamic check for the method call.
//OPTIONS: -STATIC_CHECKS

import harness.x10Test;

/**
 * Tests that a method invocation satisfies the parameter types and constraint
 * of the method declaration.
 *
 * @author pvarma
 */
public class MethodInvocation_MustFailCompile extends x10Test {

    static class Test(i:int, j:int) {
        public def this(ii:int, jj:int):Test{self.i==ii,self.j==jj} = { property(ii,jj); }
        public def tester(k:int, l:int(k)) = k + l;
    }

    public def run(): boolean = {
        var t: Test = new Test(1n, 2n);
        // the following call should fail to type check
        t.tester(3n, 4n); // ERR
        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new MethodInvocation_MustFailCompile().execute();
    }
}
