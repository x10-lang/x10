// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_10 extends x10Test {

    class C {}
    
    class It implements Iterator[C] {
        incomplete public def hasNext(): boolean;
        incomplete public def next(): C;
    }
    
    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_10().execute();
    }
}
