// (C) Copyright IBM Corporation 2009
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author igor 7/2009
 */

public class XTENLANG_472 extends x10Test {

    public def run(): boolean {
        var i:Int = 5;
        do {
            if (--i <= 0) break;
        } while(true);
        return i == 0;
    }

    public static def main(Rail[String]) {
        new XTENLANG_472().execute();
    }
}
