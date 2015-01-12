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

/**
Test that given

final double[:rank==2] buffDest = ...

the local variable declaration

double[:rank==buffDest.rank] buffSrc = new double[[1:10,1:10]->here];

does not give an error. That is, the compiler can make the inference that
(:rank==2) entails (:rank==buffDest.rank)
if buffDest is of type (:rank==2).

@author vj
**/
public class Transitivity extends x10Test {
    public def run(): boolean = {
        val buffDest: Array[double]{rank==2} = new Array[double](Region.make(1..10, 1..10));
        var buffSrc: Array[double]{rank==buffDest.rank} = new Array[double](Region.make(1..10, 1..10));

        return true;
    }
    
    public static def main(Rail[String]): void = {
        new Transitivity().execute();
    }
}
