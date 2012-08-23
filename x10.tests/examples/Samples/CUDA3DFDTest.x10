
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

public class CUDA3DFDTest extends x10Test {
    public def run():boolean {
         val args = ["16" as String, "16", "16"];
         CUDA3DFD.main(args);
         return true;
    }

    public static def main(args:Array[String](1)) {
	new CUDA3DFDTest().execute();
    }
}
