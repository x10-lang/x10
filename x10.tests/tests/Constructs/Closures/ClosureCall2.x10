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
 * A Call may to either a method or a closure. The syntax is
 * ambiguous; the target must be typechecked to determine if it is the
 * name of a method or if it refers to a closure.
 *
 * @author bdlucas 8/2008
 */

public class ClosureCall2 extends x10Test {

    def f(x:long) = "method";
    val f = (x:String) => "closure";

    public def run(): boolean {

        chk(f(1).equals("method"), "f(1)");
        chk(f("1").equals("closure"), "f(\"1\")");

        return true;
    }

    public static def main(var args: Rail[String]): void {
        new ClosureCall2().execute();
    }
}
