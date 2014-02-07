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
 * @author tardieu 01/2009
 */

class XTENLANG_138 extends x10Test {

    def f() = f(0n);

    def f(i:Int) = 0n;

    public def run(): boolean {
        val v:Int = f();
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_138().execute();
    }
}
