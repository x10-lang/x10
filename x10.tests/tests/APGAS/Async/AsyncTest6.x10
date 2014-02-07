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
 * Checking that asyncs may initialize vals
 * and that a var may be accessed from an async
 * body so long as it is declared outside of the
 * enclosing finish of the async.
 */
public class AsyncTest6 extends x10Test {

    public static N: long = 20;

    public def run(): boolean = {
        var s: long = 0;
        for (i in 0..(N-1)) {
            finish async x10.io.Console.OUT.println("s="+s+" i="+i);
            s += i;
        }
	chk(s == 190);

        val y: long;
        finish async { async y = 3; }
        x10.io.Console.OUT.println("y="+y);
	chk(y == 3);
        return true;
    }

    public static def main(Rail[String]) {
        new AsyncTest6().execute();
    }
}
