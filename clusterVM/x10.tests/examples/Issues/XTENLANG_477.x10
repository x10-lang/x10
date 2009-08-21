// (C) Copyright IBM Corporation 2009
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author igor 7/2009
 */

public class XTENLANG_477 extends x10Test {

    public def run(): boolean {
        val x = "3.14159265359";
        val d = Double.parseDouble(x);
        return d == 3.14159265359;
    }

    public static def main(Rail[String]) {
        new XTENLANG_477().execute();
    }
}

