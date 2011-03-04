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

/* STATUS: 1/21/2010 -- this file doesn't compile, because 
   it uses some unimplemented Array operations.
*/

import harness.x10Test;

public class TestSimpleArrayMult extends x10Test {

    public def run(): boolean = {

        val N: int = 99900;
        var start1: long = System.currentTimeMillis();
        val e  = 1..N;
        var regionStop: long = System.currentTimeMillis();
        val ia  = new Array[int](e, (Point)=>0);
        val ib = new Array[int](e, ([i]: Point)=>i);
        val ic  = new Array[int](e, (Point) => 2);
        var initStop: long = System.currentTimeMillis();

        for (val p: Point(1) in e) {
            ia(p) = ib(p) * ic(p);
        }

        var multStop: long = System.currentTimeMillis();
        var sum: int = sum(ia);
        var expectedValue: int = (N * (N+1));

        //expectedValue = expectedValue * 2;
        x10.io.Console.OUT.println("expected vaule:"+expectedValue);
        chk(sum == expectedValue);
        
        var regionTime: long = regionStop = start1;
        var constructTime: long = initStop - regionStop;
        var multTime: long = multStop - initStop;

        x10.io.Console.OUT.println("Region construction time:"+(regionTime/1000.0));
        x10.io.Console.OUT.println("Array construction time :"+(constructTime/1000.0));
        x10.io.Console.OUT.println("Multiplication time     :"+(multTime/1000.0));

        return true;
    }
    
    public static def sum(ia : Array[Int]) : Int {
        var s : Int = 0;
        for(i in ia) s += ia(i);
        return s;
    }

    
    public static def main(var args: Array[String](1)): void = {
        new TestSimpleArrayMult().execute();
       
    }
  
}
