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
 * Check that array deptypes are properly processed.
 *
 * @author vj
 */
public class DepTypeRef extends x10Test {
    public def run(): boolean {
      var R: Region{rect} = Region.make(1..2, 1..2);
      var a: Array[double]{rect} = new Array[double](R, (p: Point) => 1.0);
      return true;
    }

    public static def main(var args: Rail[String]): void {
        new DepTypeRef().execute();
    }
}
