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
 * @author bdlucas 12/2008
 */

import x10.io.Console;

class XTENLANG_203 extends x10Test {

    
    def foo() {throw new Exception();}

    public def run(): boolean {

        var a:boolean = true;
        var b:boolean = false;
        var c:boolean = false;
        var d:boolean = false;

        try {
            try {
                foo();
                a = false;
            } finally {
                b = true;
            }
        } catch (Exception) {
            c = true;
        } finally {
            d = true;
        }

        return a && b && c && d;
    }

    public static def main(Rail[String]) {
        new XTENLANG_203().execute();
    }
}
