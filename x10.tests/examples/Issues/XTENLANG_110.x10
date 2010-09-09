/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

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
