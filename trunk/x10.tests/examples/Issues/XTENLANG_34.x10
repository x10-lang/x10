// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_34 extends x10Test {

    static class C {}
    
    public def run():boolean {
        val o:Object = 1.0;
        val x = o as int;
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_34().execute();
    }
}
