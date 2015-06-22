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
 * The closure body has the same syntax as a method body; it may be
 * either an expression, a block of statements, or a block terminated
 * by an expression to return.
 *
 * @author bdlucas 8/2008
 */

public class ClosureBody1a extends x10Test {

    public def run(): boolean {
        
        // expression
        val f = ()=>1+1;
        chk(f() == 2l, "f()");

        return true;
    }

    public static def main(var args: Rail[String]): void {
        new ClosureBody1a().execute();
    }
}
