/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

import harness.x10Test;
import x10.regionarray.*;


/**
 * Check the condition of an if is flattened.
 */

public class FlattenConditional3 extends x10Test {

    var a: Array[int](2);

    public def this(): FlattenConditional3 = {
        a = new Array[int](Region.make(1..10, 1..10), ([i,j]: Point) => { return (i+j) as int;});
    }
    
    def m(var a: int): int = {
        if (a == 2n) throw new Exception();
        return a;
    }
    
    public def run(): boolean = {
    var b: int = 0n;
        if (a(2, 2) == 0n) b=1n;
            return b==0n;
    }

    public static def main(Rail[String])  {
        new FlattenConditional3().execute();
    }
}
