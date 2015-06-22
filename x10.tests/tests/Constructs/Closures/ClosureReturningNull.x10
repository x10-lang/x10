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
 * Check that a closure literal returning null can be properly
 * invoked when assigned to types
 */
public class ClosureReturningNull extends x10Test {
    val cls = ()=>null;

    public def run(): boolean {
        val t = cls();
        chk(t == null);
        chk (test1(cls) == null);
        chk (test2(cls) == null);

        return true;
    }

    public def test1(c:()=>Any) = c();
    public def test2(c:()=>String) = c();

    public static def main(var args: Rail[String]): void {
        new ClosureReturningNull().execute();
    }
}
