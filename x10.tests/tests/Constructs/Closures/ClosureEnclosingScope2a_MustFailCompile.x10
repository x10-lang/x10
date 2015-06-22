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
 * STATIC SEMANTICS RULE: In an expression (x1: T1, . . ., xn: Tn) =>
 * e, any outer local variable accessed by e must be final or must be
 * declared as shared (§14.9).
 *
 * @author bdlucas 8/2008
 */

public class ClosureEnclosingScope2a_MustFailCompile extends x10Test {

    

    public def run(): boolean {
    		var a:long = 1;

        chk((()=>a) // ERR
            () == 1, "(()=>a)()");

        return true;
    }

    public static def main(var args: Rail[String]): void {
        new ClosureEnclosingScope2a_MustFailCompile().execute();
    }
}
