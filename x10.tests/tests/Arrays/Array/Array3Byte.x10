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
 * Ensures byte arrays are implemented.
 */
public class Array3Byte extends x10Test {

    public def run(): boolean = {
        val r  = Region.make(1..10, 1..10);
        val ia  = new Array[Byte](r, (x:Point)=>(0 as Byte));
    
        ia(1, 1) = 42 as Byte;
        return (42y == ia(1, 1));
    }

    public static def main(var args: Rail[String]): void = {
        new Array3Byte().execute();
    }
}
