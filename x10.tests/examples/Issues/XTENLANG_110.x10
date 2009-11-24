// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_110 extends x10Test {

    class C {}
    
    class D extends C {}
    
    class CS implements Iterable[C] {
        incomplete public def iterator(): Iterator[C];
    }
    
    class DS implements Iterable[D] {
        incomplete public def iterator(): Iterator[D];
    }
    
    val cs = new CS();
    val ds = new DS();
    
    def foo() {
        for (c:C in ds)
            ;
    }

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_110().execute();
    }
}
