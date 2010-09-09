// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

import x10.io.Reader;
    
class XTENLANG_37 extends x10Test {

    def foo(is:Reader) {
        try {
            is.read();
        } catch (Exception) {}
    }

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_37().execute();
    }
}
