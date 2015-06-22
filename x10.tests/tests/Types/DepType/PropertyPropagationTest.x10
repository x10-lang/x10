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
 * Check that if D is of type Dist(R), and E of type Dist(R2), and R and R2
   have the same rank, then D and E have the same rank.
   @author vj 09/2008
 */
public class PropertyPropagationTest extends x10Test {
    public def run(): boolean {
        val R = Region.make(1..10, 1..10);
        val R2 = Region.make(1..101, 1..101);
        val D = Dist.makeBlock(R);
        val E = Dist.makeBlock(R2);
        // val F = D || E; removed because || removed on Dist.
        val f = D.isSubdistribution(E);

        return true;
    }

    public static def main(var args: Rail[String]): void {
        new PropertyPropagationTest().execute();
    }
}
