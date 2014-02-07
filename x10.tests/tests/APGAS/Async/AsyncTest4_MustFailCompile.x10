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
 * An async cannot access a var variable unless there is
 * a finish "between" the async and the var declaration.
 */
public class AsyncTest4_MustFailCompile extends x10Test {

    public static N: int = 20n;

    public def run(): boolean = {
        finish {
            var s: int = 0n;
            for (var i: int = 0n; i < N; i++) {
                //==> compiler error expected here
                async x10.io.Console.OUT.println("s="+
                    s+ // ERR: cannot be captured in an async if there is no enclosing finish in the same scoping-level
                    " i="+
                    i); // ERR: cannot be captured in an async if there is no enclosing finish in the same scoping-level
                s += i;
            }
        }

        var s:int = 0n;
        for (var i: int = 0n; i < N; i++) {
            // no compiler error here because s declared outside the finish
            finish async x10.io.Console.OUT.println("s="+s+" i="+i);
            s += i;
        }
        return true;
    }

    public static def main(Rail[String]) {
        new AsyncTest4_MustFailCompile().execute();
    }
}
