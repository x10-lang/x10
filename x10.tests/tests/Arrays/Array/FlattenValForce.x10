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

import x10.util.concurrent.Future;
/**
 * Test for array reference flattening. Check that flattening works inside a Future.
 */

public class FlattenValForce extends x10Test {
   
    static def rd(val e: Array[Future[Int]](1), val i: int)   {
        val fd = Future.make[Double]( ()=> 3.0);
        val x  = fd();
        return Future.make[Int](()=> e(i)());
    }
   
    public def run(): boolean = true;

    public static def main(Rail[String]){
        new FlattenValForce().execute();
    }
}
