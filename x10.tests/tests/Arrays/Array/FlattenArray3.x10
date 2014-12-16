/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import harness.x10Test;
import x10.regionarray.*;

/**
 * Test for array reference flattening. Checks that after flattening
 * the variable x and y can still be referenced, i.e. are not 
 * declared within local blocks.
 *
 * To check that this test does what it was intended to, examine
 * the output Java file. It should have a series of local variables
 * pulling out the subters of m(a[1,1]).
 */
 
public class FlattenArray3 extends x10Test {

    var a: Array[int](2);

    public def this(): FlattenArray3 = {
        a = new Array[int](Region.make(1..10, 1..10), ([i,j]: Point) => { return (i+j) as int;});
    }

    def m(var x: int): int = {
        return x;
    }

    public def run(): boolean = {
        var x: int = m(a(1, 1)); // being called in a method to force flattening.
        var y: int = m(a(2, 2));
        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new FlattenArray3().execute();
    }
    
}
