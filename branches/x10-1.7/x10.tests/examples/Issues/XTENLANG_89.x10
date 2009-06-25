// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_89 extends x10Test {

    static class C {}
    
    public def run(): boolean {
        val c = new C();
        x10.io.Console.OUT.println("c " + c);
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_89().execute();
    }
}
