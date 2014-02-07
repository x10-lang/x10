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
 * @author bdlucas 10/2008
 */
class XTENLANG_133 extends x10Test {

    public def run():boolean {
        val d1 = Dist.makeConstant(Region.make(0..2, 0..3));
        val d2 = Dist.makeConstant(Region.make(-1, -2), here);
        d1.equals(d2);
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_133().execute();
    }
}
