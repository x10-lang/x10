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
import x10.util.concurrent.Future;

/**
 * Testing arrays of Future<T>.
 *
 * @author kemal, 5/2005
 */

public class ArrayFuture extends x10Test {

    public def run(): boolean = {
        val d = Dist.makeConstant(Region.make(1..10, 1..10), here);
        val ia  = DistArray.make[Future[long]](d, ([i,j]: Point) => Future.make[long](()=>{i+j}));
        for ([i,j]: Point in ia.region) chk(ia(i, j)() == i+j);
        return true;
    }

    public static def main(Rail[String]): void = {
        new ArrayFuture().execute();
    }
}
