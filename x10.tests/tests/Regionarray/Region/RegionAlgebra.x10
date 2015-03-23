/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 *  (C) Copyright Australian National University 2011.
 */
import harness.x10Test;
import x10.regionarray.*;

/**
 * Region algebra.
 *
 * @author kemal 4/2005
 */
public class RegionAlgebra extends x10Test {
    public def run(): boolean {
        val R1: Region(2) = Region.make(0..1, 0..7);
        val R2: Region(2) = Region.make(4..5, 0..7);
        val R3: Region(2) = Region.make(0..7, 4..5);

        chk(R1.disjoint(R2));

        val T1 = R1 && R3;
        chk(R1.contains(T1));
        chk(R3.contains(T1));

        // TODO uncomment when Region.union() is restored
        //val T2: Region(2) = (R1 || R2) && R3;
        //chk(T2.equals( (0..1 * 4..5) || (4..5 * 4..5) ));
        //chk((R1 || R2).contains(T2) && R3.contains(T2));
        //val T3: Region(2) = R1 || R2 || R3;
        //chk(T3.equals((0..1 * 0..7) || (4..5 * 0..7) || (2..3 * 4..5) || (6..7 * 4..5)));
        //chk(T3.contains(R1) && T3.contains(R2) && T3.contains(R3));
        //val T4: Region(2) = (R1 || R2) - R3;
        //chk(T4.equals((0..1 * 0..3) || (0..1 * 6..7) || (4..5 * 0..3) || (4..5 * 6..7)));
        //chk((R1 || R2).contains(T4) && T4.disjoint(R3));

        val R1_0 = R1.projection(0);
        chk(R1_0.equals(Region.make(0, 1)));

        val R1_1 = R1.eliminate(0);
        chk(R1_1.equals(Region.make(0, 7)));

        val R4 = Region.make(0..1, 0..2, 0..3, 0..4);
        val R4_e2 = R4.eliminate(2);
        chk(R4_e2.equals(Region.make(0..1, 0..2, 0..4)));

        val R5 = R1.translate(Point.make(1,1));
        chk(R5.equals(Region.make(1..2, 1..8)));

        return true;
    }

    public static def main(var args: Rail[String]): void {
        new RegionAlgebra().execute();
    }
}
