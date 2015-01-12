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
 * @author igorp 06/2009
 * @author nystrom 06/2009
 */

class XTENLANG_455 extends x10Test {

    static class X {
        public def m[T](x:T): void { }
    }

    static class Y extends X {
        public def m(x:String) = "Correct";
    }

    public def run():boolean {
        val y = new Y();
        val z: String = y.m("Incorrect");
        return z.equals("Correct");
    }

    public static def main(Rail[String]) {
        new XTENLANG_455().execute();
    }
}


