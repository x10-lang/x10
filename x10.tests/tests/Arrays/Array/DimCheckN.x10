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
 * A rank 2 array can be initialized with a point of rank 1. The
 * pattern (i) merely checks that the point has rank >= 1.
 *
 * @author vj 12 2006
 */

public class DimCheckN extends x10Test {

    public def run(): boolean = {
        val a1  = new Array[long](Region.make(0..2, 0..3), ([i,j]: Point) => i);
        return true;
    }
    
    public static def main(var args: Rail[String]): void = {
        new DimCheckN().execute();
    }
}
