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
 * Test for array reference flattening. Checks that after flattening
 * the variable x and y can still be referenced, i.e. are not 
 * declared within local blocks.
 */

public class FlattenCast extends x10Test {

    var a: Array[int](2);

    public def this(): FlattenCast = {
        a = new Array[int](Region.make(1..10, 1..10), (p[i,j]: Point) => (i+j) as Int);
    }

    def m(x: int)=x;

    public def run(): boolean = {
        val x =  m(a(1, 1)) as Double; // being called in a method to force flattening.
        return 2.0==x;
    }
    
    public static def main(var args: Rail[String]): void = {
        new FlattenCast().execute();
    }
}
