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
import x10.array.Region;

/**
 * Region algebra.
 *
 * @author kemal 4/2005
 */

public class RegionAlgebra extends x10Test {

    // need fix for XTENLANG-51, or change test

    public def run(): boolean = {
        val R1: Region{rank==2} = [0..1, 0..7];
        val R2: Region{rank==2} = [4..5, 0..7];
        val R3: Region{rank==2} = [0..7, 4..5];
        val T1: Region{rank==2} = (R1 || R2) && R3;
        chk(T1.equals(([0..1, 4..5] as Region{rank==2}) || [4..5, 4..5]));
        chk((R1 || R2).contains(T1) && R3.contains(T1));
        val T2: Region{rank==2} = R1 || R2 || R3;
        chk(T2.equals(([0..1, 0..7] as Region{rank==2}) || [4..5, 0..7] || [2..3, 4..5] || [6..7, 4..5]));
        chk(T2.contains(R1) && T2.contains(R2) && T2.contains(R3));
        val T3: Region{rank==2} = (R1 || R2) - R3;
        chk(T3.equals(([0..1, 0..3] as Region{rank==2}) || [0..1, 6..7] || [4..5, 0..3] || [4..5, 6..7]));
        chk((R1 || R2).contains(T3) && T3.disjoint(R3));
        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new RegionAlgebra().execute();
    }
}
