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
 *
 * Check that a field can be declared at a deptype.
 *
 */
public class FieldDepType extends x10Test {
    var f: Array[Double](1) = new Array[double](11, (i:long)=> (10-i) as Double);

    def m(a: Array[Double]{rank==1&&rect&&zeroBased}): void = {
    }
    public def run(): Boolean = {
        m(f as Array[Double]{zeroBased, rect, rank==1});
        return f(0)==10.0D;
    }
    public static def main(Rail[String]): void = {
        new FieldDepType().execute();
    }
}
