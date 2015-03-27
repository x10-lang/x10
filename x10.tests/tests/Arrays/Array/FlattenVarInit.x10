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
 * Java and X10 permit a call to a method which returns a value to occur as a statement.
 * The returned value is discarded. However, Java does not permit a variable standing alone
 * as a statement. Thus the x10 compiler must check that as a result of flattening it does
 * not produce a variable standing alone. 
 * In an earlier implementation this would give a t0 not reachable error.
 */

public class FlattenVarInit extends x10Test {

    val a: Array[int](2);

    public def this()  {
        a = new Array[int](Region.make(1..10, 1..10), ([i,j]: Point) => (i+j) as Int);
    }

    def m(x: int)=x;
    
    public def run(): boolean {
        var t0: int;
        t0 = m(a(1, 1));
        return t0==2n;
    }

    public static def main(Rail[String]) {
        new FlattenVarInit().execute();
    }
}
