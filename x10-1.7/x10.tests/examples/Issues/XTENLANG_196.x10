// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 12/2008
 */

class XTENLANG_196 extends x10Test {

    public def run(): boolean {
        val o1:Object = 17;
        val o2:Object = 3.2;
        val o3:Object = "bye";
        val x = String.format("hi %d %8.3f %s", [o1,o2,o3]);
        x10.io.Console.OUT.println(x);
        return x.equals("hi 17    3.200 bye");
    }

    public static def main(Rail[String]) {
        new XTENLANG_196().execute();
    }
}
