// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_55 extends x10Test {

    class C {}
    
    def foo(it:Iterator[C]) {
        val c:C = it.next();
    }

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_55().execute();
    }
}
