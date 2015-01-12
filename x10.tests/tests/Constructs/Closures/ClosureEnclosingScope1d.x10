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
 * The body s in a function (x1: T1, . . ., xn: Tn) => { s } may access
 * fields of enclosing classes and local variable declared in an outer
 * scope.
 *
 * @author bdlucas 8/2008
 */

public class ClosureEnclosingScope1d extends x10Test {

    val a = 1;

    public def run(): boolean = {
        
        val b = 1;

        class C {
            val c = 1;
            def foo() = {
                val fun = () => {
                    val d = 1;
                    (() => a+b+c+d)()
                };
                fun()
            }
        }

        chk(new C().foo() == 4, "new C().foo()");

        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureEnclosingScope1d().execute();
    }
}
