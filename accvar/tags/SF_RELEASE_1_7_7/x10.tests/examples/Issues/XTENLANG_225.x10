// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 12/2008
 */

class XTENLANG_225 extends x10Test {

    abstract class R {
        abstract def run(): String;
    }
    
    def foo() {
        new R() {
            def run() = "";
        };
    }
    
    public def run():boolean {
        foo();
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_225().execute();
    }
}
