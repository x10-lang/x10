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

// NUM_PLACES: 4

/**
 * When the async place is not specified, it is take to be 'here'.
 *
 * @author Kemal Ebcioglu 4/2005
 */
public class AsyncTest5 extends x10Test {

    public def run(): boolean = {
        val A: DistArray[int](1) = DistArray.make[int](Dist.makeUnique());
        chk(Place.numPlaces() >= 2);
        finish async chk(A.dist(0) == here);
        // verify unique distribution
        for (val [i]: Point in A)
            for (val [j]: Point in A)
                chk(implies(A.dist(i) == A.dist(j), i == j));

        // verify async S is async(here)S
        finish ateach (val [i]: Point in A) {
            async { atomic A(i) += (i as int);
                chk(A.dist(i) == here);
                async at(Place.FIRST_PLACE) async chk(A.dist(0) == here);
            }
        }
        finish ateach (val [i]: Point in A) {
            chk(A(i) == i as int);
        }
        return true;
    }

    static def implies(var x: boolean, var y: boolean): boolean = {
        return (!x) | y;
    }

    public static def main(var args: Rail[String]): void = {
        new AsyncTest5().execute();
    }
}
