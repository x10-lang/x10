// (C) Copyright IBM Corporation 2009
// This file is part of X10 Test. *

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


