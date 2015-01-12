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
 * Tests that a closure occurring in DepType causes a compilation failure.
 *
 * @author igor
 */
public class ClosureInClassDepClause_MustFailCompile(p:()=>Int){p== // ERR ERR: Cannot build constraint from expression
        (()=>3n) // ERR: Closure cannot occur outside code body.
        } extends x10Test {

    public def run() = true;

    public static def main(var args: Rail[String]): void = {
        new ClosureInClassDepClause_MustFailCompile(()=>3).execute();
    }
}
