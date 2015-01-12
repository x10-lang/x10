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

import x10.util.Box;
import harness.x10Test;

/**
 * @author igor 7/2009
 */

public class XTENLANG_473 extends x10Test {

    public static class G[T] {
        public def foo(x: Box[T]): T {
            val y = x();
            return y;
        }
    }

    public def run(): boolean {
        val x: Box[String] = new Box[String]("aaa");
        val y = new G[String]().foo(x);
        val z = x();
        return y == z;
    }

    public static def main(Rail[String]) {
        new XTENLANG_473().execute();
    }
}

