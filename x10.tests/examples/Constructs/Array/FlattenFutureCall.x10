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
 * Test for array reference flattening. Checks that after flattening
 * the variable x and y can still be referenced, i.e. are not 
 * declared within local blocks.
  
 * To check that this test does what it was intended to, examine
 * the output Java file. It should have a series of local variables
 * pulling out the subters of m(a[1,1]).
 */
 
public class FlattenFutureCall extends x10Test {

    val a: DistArray[int](2);

    public def this(): FlattenFutureCall = {
        val reg = 1..10*1..10;
        val d = reg->here;
        a = DistArray.make[int](d, ([i,j]: Point): int => i+j);
    }
    
    public def run(): boolean = {
        var x: boolean = at(a.dist(1, 1)){ true};
        return x;
    }

    public static def main(var args: Array[String](1)): void = {
        new FlattenFutureCall().execute();
    }
}
