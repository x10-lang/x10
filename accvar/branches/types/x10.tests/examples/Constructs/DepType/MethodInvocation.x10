/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

import harness.x10Test;

/**
 * Tests that a method invocation satisfies the parameter types and constraint
 * of the method declaration.
 *
 * @author pvarma
 */
public class MethodInvocation extends x10Test {

    static class Test(i:int, j:int) {
        public def this(ii:int, jj:int):Test{self.i==ii,self.j==jj} = { property(ii,jj); }
        public def tester(k:int, l:int(k)) = k + l;
    }

    public def run(): boolean = {
        var t: Test = new Test(1, 2);
        // the following call types correctly
        t.tester(3, 3);
        return true;
    }

    public static def main(var args: Array[String](1)): void = {
        new MethodInvocation().execute();
    }
}
