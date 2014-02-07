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

public class Array1DCodeGen extends x10Test {

    final def matgen(val a: DistArray[double](3), val b: DistArray[double](3)): double = {

        var n: long = a.region.max(0);
        var init: int = 1325n;
        var norma: double = 0.0;

        /* Next two for() statements switched.  Solver wants
           matrix in column order. --dmd 3/3/97
         */

        for ([i,j,k]: Point(3) in a) {
            init = 3125n*init % 65536n;
            var value: double = (init - 32768.0)/16384.0;
            finish write(a, i, j, k, value);
            norma = value > norma ? value : norma;
        }

        finish ateach ([i,j,k]: Point(3) in b.dist) b(i, j, k) = 0.0;
        finish ateach ([i,j,k]: Point(3) in a.dist | Region.make(0..(n-1), 0..(n-1), 0..(n-1))) plusWrite(b, 0, j, k, a(i, j, k));

        return norma;
    }

    final def write(val a: DistArray[double](3), val i: long, val j: long, val k: long, val val_: double): void = {
        async at(a.dist(i, j, k)) atomic a(i, j, k) = val_;
    }

    final 
    static // BARD: This failed to compile with a place error.
           // So I made it static.
    def plusWrite(val a: DistArray[double](3), val i: long, val j: long, val k: long, val val_: double): void = {
        async at(a.dist(i, j, k)) atomic a(i, j, k) += val_;
    }
    
    public def run(): boolean = {

        val R = Region.make(0..5, 0..5, 0..5);
        val a = DistArray.make[double](Dist.makeConstant(R, here));
        val b = DistArray.make[double](Dist.makeConstant(R, here));

        x10.io.Console.OUT.println("runtime type of 3dZeroBasedRect array is " + a.typeName());

        val result = matgen(a,b);
        val S = Region.make(0..5, 0..5, 0..5);
        val aa =  DistArray.make[double](Dist.makeConstant(S, here));
        val bb =  DistArray.make[double](Dist.makeConstant(S, here));
        var result1: double = matgen(aa,bb);

        x10.io.Console.OUT.println("runtime type of unknown array is " + aa.typeName());
        x10.io.Console.OUT.println("results are " + result + " " + result1);

        var diff: double = result-result1;

        return diff < 0 ? diff > -0.001 : diff < 0.001;
    }

    public static def main(Rail[String]) {
        new Array1DCodeGen().execute();
    }
}
