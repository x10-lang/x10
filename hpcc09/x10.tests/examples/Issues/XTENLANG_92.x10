// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_92 extends x10Test {

    static class C implements Iterable[int] {
        incomplete public def iterator(): Iterator[int];
    }

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_92().execute();
    }
}
