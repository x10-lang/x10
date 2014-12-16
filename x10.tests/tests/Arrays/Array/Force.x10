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
 * Test for array reference flattening. 
 */
import x10.util.concurrent.Future;

public class Force extends x10Test {
   
    static def rd(val e: Future[Int], val i: int, val j: int): int = {
        val x: int = e();
        return Future.make[int](()=> x)();
    }
   
    public def run(): boolean = true;

    public static def main(Rail[String])  {
        new Force().execute();
    }
}
