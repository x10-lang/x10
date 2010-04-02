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
import x10.array.Dist;

/**
 * Testing that disjoint union of dists
 * actually checks for disjointness.
 *
 * @author kemal 4/2005
 */

public class DistAlgebra3 extends x10Test {

    const N = 24;

    public def run(): boolean = {
        val D  = Dist.makeCyclic(0..N-1);
        val D2 = D | 0..N/2-1;
        val D3 = D | (N/2)..N-1; 
        val D4 = D2 || D3; // disjoint
        chk(D4.equals(D));
        try {
            val D5 = D || D2; // not disjoint
        } catch (e: IllegalOperationException) {
            return true;
        }
        return false;
    }

    public static def main(var args: Rail[String]): void = {
        new DistAlgebra3().execute();
    }
}
