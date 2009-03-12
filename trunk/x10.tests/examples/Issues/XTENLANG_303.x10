// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 12/2008
 */

class XTENLANG_303 extends x10Test {

    public def run(): boolean {
        try {
            throw new Exception("hi");
        } catch (e: Exception) {
            x10.io.Console.OUT.println("e: " + e);
        }
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_303().execute();
    }
}
