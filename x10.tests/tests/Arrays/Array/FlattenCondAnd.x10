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
 * Conditional and is evaluated, well, conditionally. So it must be
 * translated thus:
 *
   c = a && b
   =>
   <stmt-a>
   boolean result = a;
   if (a) {
     <stmt-b>;
     result = b;
     }
     c = result;
 *
 * @author vj
 */

public class FlattenCondAnd extends x10Test {

    val a: Array[Boolean](2);

    public def this(): FlattenCondAnd {
        a = new Array[Boolean](Region.make(1..10, 1..10), ([i,j]: Point) => true);
    }

    def m(x: boolean)= !x;

    public def run(): boolean {
        var x: boolean = m(a(1, 1)) && a(0, 0); // the second expression will throw an exception if it is evaluated.
        return !x;
    }

    public static def main(Rail[String]) {
        new FlattenCondAnd().execute();
    }
}
