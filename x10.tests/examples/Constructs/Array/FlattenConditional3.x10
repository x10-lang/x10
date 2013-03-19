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
import x10.array.*;


/**
 * Check the condition of an if is flattened.
 */

public class FlattenConditional3 extends x10Test {

    var a: Array[int](2);

    public def this(): FlattenConditional3 = {
        a = new Array[int]((1..10)*(1..10), ([i,j]: Point) => { return i+j;});
    }
    
    def m(var a: int): int = {
        if (a == 2) throw new Exception();
        return a;
    }
    
    public def run(): boolean = {
    var b: int = 0;
        if (a(2, 2) == 0) b=1;
            return b==0;
    }

    public static def main(Rail[String])  {
        new FlattenConditional3().execute();
    }
}
