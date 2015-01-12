
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

// SOURCEPATH: x10.dist/samples/CUDA
// NUM_PLACES: 2
// SKIP_MANAGED_X10: XTENLANG-3358 : scoping bug in boxed vars introduced for async init

public class KMeansCUDATest extends x10Test {
    public def run():boolean {
         val args = new Rail[String](2);
         args(0) = "-p"; 
         args(1) = pathCombine(["tests", "Samples"], "points.dat");
         KMeansCUDA.main(args);
         return true;
    }

    public static def main(args:Rail[String]) {
	new KMeansCUDATest().execute();
    }
}
