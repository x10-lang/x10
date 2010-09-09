// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 12/2008
 */

import x10.io.Console;

class XTENLANG_202 extends x10Test {

    
    public def run(): boolean {
        return "foo".hashCode()!=0;
    }

    public static def main(Rail[String]) {
        new XTENLANG_202().execute();
    }
}
