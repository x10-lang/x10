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
 * 
 */

public class FlattenConditional2 extends x10Test {

    var a: Array[int](2);

    public def this(): FlattenConditional2 = {
        a = new Array[int](Region.make(1..10, 1..10), ([i,j]: Point) => { return (i+j) as int;});
    }

    var extra: int = 4n;

    def m(i: int): int = {
        if (i==6n) throw new Exception();
        return i;
    }

    public def run(): boolean = {
        var x: int = a(1, 1)==2n? m(a(2, 2)) : m(a(3, 3));
        return x==4n;
    }

    public static def main(Rail[String])  {
        new FlattenConditional2().execute();
    }
}
