// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 12/2008
 */

class XTENLANG_164 extends x10Test {

    public def run():boolean {
        var x:Iterable[int] = [1,2,3];
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_164().execute();
    }
}
