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
 * This must compile and run fine. Checks that the initializer may not specify
 * the arity of the region.

 * @author vj 12 2006
 */

public class DimCheck extends x10Test {

    public def run() {
        var a1: Array[long] = new Array[long](Region.make(0..2, 0..3), (p: Point) => p(0));
        return true;
    }

    public static def main(var args: Rail[String]): void {
        new DimCheck().execute();
    }
}
