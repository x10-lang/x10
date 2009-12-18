// (C) Copyright IBM Corporation 2009
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author igor 7/2009
 */

public class XTENLANG_473 extends x10Test {

    public static class G[T] {
        public def foo(x: Box[T]): T {
            val y = x as T;
            return y;
        }
    }

    public def run(): boolean {
        val x: Box[String] = "aaa";
        val y = new G[String]().foo(x);
        val z = x as String;
        return y == z;
    }

    public static def main(Rail[String]) {
        new XTENLANG_473().execute();
    }
}

