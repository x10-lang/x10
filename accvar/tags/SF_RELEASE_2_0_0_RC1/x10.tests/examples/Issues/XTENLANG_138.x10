// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author tardieu 01/2009
 */

class XTENLANG_138 extends x10Test {

    def f() = f(0);

    def f(i:Int) = 0;

    public def run(): boolean {
        val v:Int = f();
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_138().execute();
    }
}
