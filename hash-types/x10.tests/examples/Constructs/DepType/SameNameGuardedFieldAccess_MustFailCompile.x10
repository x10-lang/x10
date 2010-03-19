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
 * Tests that two guarded fields may not have the same name, even if their guard
 * conditions are disjoint.
 *
 * @author pvarma
 */
public class SameNameGuardedFieldAccess_MustFailCompile extends x10Test {

    static class Test(i: int, j:int) {
        public var v{i==5}: int = 5;
        public var v{i==6}: int = 6;
        def this(i:int, j:int):Test = {
            this.i=i;
            this.j=j;
        }
    }

    public def run(): boolean = {
        var t: Test{self.i==5} = new Test(5, 5);
        t.value = t.value + 10;
        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new SameNameGuardedFieldAccess_MustFailCompile().execute();
    }
}
