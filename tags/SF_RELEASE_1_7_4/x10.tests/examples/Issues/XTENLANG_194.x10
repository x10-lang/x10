// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 12/2008
 */

import x10.util.HashMap;
    
class XTENLANG_194 extends x10Test {

    val h = new HashMap[Value,Value]();

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_194().execute();
    }
}
