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
 * 
 */

public class FlattenInitFor extends x10Test {

    global val a: Array[int](2);

    public def this(): FlattenInitFor = {
        a = Array.make[int](([1..10, 1..10] as Region)->here, ((i,j): Point): int => { return i;});
    }
    
    public def run(): boolean = {
        for (var e: int = (future (a.dist(1, 1)) { a(1, 1) }).force(); e < 3 ; e++) 
            x10.io.Console.OUT.println("done.");        
        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new FlattenInitFor().execute();
    }
}
