// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 12/2008
 */

class XTENLANG_257 extends x10Test {

    static class E {}
    
    public def run():boolean {
        val e = new E();
        x10.io.Console.OUT.println("e.home() " + e.home());
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_257().execute();
    }
}
