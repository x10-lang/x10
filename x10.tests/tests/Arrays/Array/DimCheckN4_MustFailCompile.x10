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

//LIMITATION:
//This check is not being done by the compiler currently.
import harness.x10Test;
import x10.regionarray.*;

/**
 *
 * @author vj 12 2006
 */
public class DimCheckN4_MustFailCompile extends x10Test {

    def m(d: Dist(2)): void = {
        val a1 = DistArray.make[int](d, (p[i,j,k]: Point(3)): int => { return i as int; }); // ERR (dimension mismatch)
        val a2 = DistArray.make[int](d, (p[i,j,k]: Point(2)): int => { return i as int; }); // ERR
    }

    public def run(): boolean = {
        m(Region.make(0..2, 0..3) -> here);
        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new DimCheckN4_MustFailCompile().execute();
    }
}
