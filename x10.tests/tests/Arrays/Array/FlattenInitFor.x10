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

public class FlattenInitFor extends x10Test {

    val a: DistArray[int](2);

    public def this(): FlattenInitFor {
        a = DistArray.make[int](Region.make(1..10, 1..10)->here, ([i,j]: Point): int => (i as int));
    }
    
    public def run(): boolean {
        for (var e: int = at (a.dist(1, 1)) a(1, 1); e < 3 ; e++) 
            x10.io.Console.OUT.println("done.");        
        return true;
    }

    public static def main(Rail[String]) {
        new FlattenInitFor().execute();
    }
}
