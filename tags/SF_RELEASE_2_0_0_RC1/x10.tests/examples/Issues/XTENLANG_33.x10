// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_33 extends x10Test {

    static class C {}
    
    public def run():boolean {
        val c:C= null;
        val o:Object = c;
        val x = o as C;
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_33().execute();
    }
}
