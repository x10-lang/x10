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
 * The test checks that final local variables can be accessed in a depclause.
 *
 * @author vj
 */
public class VariableMustBeFinalInDepClause extends x10Test {
    static class Test(i:int) {
        public def this(ii:int):Test{self.i==ii} {
            property(ii);
        }
    }
    public def m(var t: Test{i==52n}): Test{i==52n} {
        val j: int{self==52n} = 52n;
        var a: Test{i==j} = t;
        return a;
    }
    public def run(): boolean {
        var t: Test{i==52n} = new Test(52n);
        return m(t).i==52n;
    }
    public static def main(var args: Rail[String]): void {
        new VariableMustBeFinalInDepClause().execute();
    }

}
