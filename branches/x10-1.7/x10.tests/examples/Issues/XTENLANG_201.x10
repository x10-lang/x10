// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 12/2008
 */

class XTENLANG_201 extends x10Test {

    public def run(): boolean {
        try {
            val r = Rail.makeVar[Rail[int]](100);
            for (var i:int=0; i<100; i++)
                r(i) = Rail.makeVar[int](1000*1000*1000);
        } catch (Error) {
            return true;
        }
        return false;
    }

    public static def main(Rail[String]) {
        new XTENLANG_201().execute();
    }
}
