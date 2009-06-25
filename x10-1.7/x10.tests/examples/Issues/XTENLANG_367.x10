// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author igor 4/2009
 */

class XTENLANG_367 extends x10Test {

    public def run(): boolean {
        var count: int = 0;
        try {
            try {
                count++;
                throw new Exception();
            } catch (e: Exception) {
                count++;
            } finally {
                count++;
                throw new RuntimeException();
            }
        } catch (e: RuntimeException) { }
        return count == 3;
    }

    public static def main(Rail[String]) {
        new XTENLANG_367().execute();
    }
}
