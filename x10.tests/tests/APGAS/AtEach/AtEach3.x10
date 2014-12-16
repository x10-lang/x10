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
 * Test ateach on the points of a DistArray using 
 * point destructuring syntax.
 */
public class AtEach3 extends x10Test {

    public def run(): boolean = {
        val R:Region(2) = Region.make(1..10, 1..10);
        val D:Dist(2) = Dist.makeBlock(R);
        val data:DistArray[Long](2) = DistArray.make[Long](D, ([i,j]:Point)=>i+j);

        finish ateach([i,j]:Point(2) in data) {
            chk(data(i,j) == i+j);
        }        

        return true;
    }

    public static def main(Rail[String])  {
        new AtEach3().execute();
    }
}
