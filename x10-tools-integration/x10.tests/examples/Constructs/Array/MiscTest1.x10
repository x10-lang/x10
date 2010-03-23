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

/* STATUS: 1/21/2010 -- this file doesn't compile, because 
   it uses some unimplemented Array operations.
*/

import harness.x10Test;

/**
 * Tests miscellaneous features together: async, future, atomic,
 * distributed array, dist ops, reduction, scan, lift.
 *
 * @author kemal 12/2004
 */

public class MiscTest1 extends x10Test {

    public const N: int = 50;

    public const NP: int = Place.MAX_PLACES;

    public def run(): boolean = {

        val R: Region{rank==1} = [0..NP-1];

        // verify that a blocked dist for
        // (0..MAX_PLACES-1) is a unique dist
        // verify that a cyclic dist for
        // (0..MAX_PLACES-1) is again the same
        val D: Dist{rank==1} = Dist.makeBlock(R,0) as (Dist{rank==1});
        val D2: Dist = Dist.makeUnique(Place.places);
        val D3: Dist = Dist.makeCyclic(R, 0);

        chk(D.equals(D2));
        chk(D.equals(D3));

        // create zero int array x
        val x: Array[int]{dist==D} = Array.make[int](D);


        // set x[i] = N*i with N atomic updates
        finish
            for (pi(i) in R)
                for (val pj in [0..N-1])
                    async(D(pi))
                        atomic x(pi) += i;


        // ensure sum = N*SUM(int i = 0..NP-1)(i);
        // == N*((NP*(NP-1))/2)
        val sum: int = x.sum();
        chk(sum == (N*NP*(NP-1)/2));


        // also verify each array elem x[i] == N*i;
        // test D|R restricton and also D-D1

        val r_inner: Region{rank==1} = [1..NP-2];
        val D_inner: Dist{rank==1} = D|r_inner;
        val D_boundary: Dist{rank==1} = D-r_inner;
        finish {
            ateach (val pi(i) in D_inner) {
                chk(x(pi) == N*i);
                chk(x(i) == N*i);
                chk(D(pi) == D_inner(pi) && D(pi) == here);
            }
        }

        finish {
            ateach (val pi(i) in D_boundary) {
                chk(x(pi) == N*i);
                chk(D(pi) == D_boundary(pi) && D(pi) == here);
            }
        }

        // test scan
        val y: Array[int]{dist==D} = x.scan(Int.+, 0);

        // y[i] == x[i]+y[i-1], for i>0
        finish {
            ateach (pi(i) in D) {
                val pi1: Point(1) = [i-1];
                chk(y(pi) == x(pi) + (i == 0 ? 0 : (future(D(pi1)) y(pi1)).force()));
                chk(y(i) == x(i) + (i == 0 ? 0 : (future(D(i-1)) y(i-1)).force()));
            }
        }

        // y[NP-1] == SUM(x[0..NP-1])
        val pNP_1: Point(1) = [NP-1];

        chk(sum == (future(D(pNP_1)) y(pNP_1)).force());
        chk(sum == (future(D(NP-1)) y(NP-1)).force());

        // test lift
        val z: Array[int]{dist==D} =  x.lift(Int.+, y) as Array[int]{dist==D};

        finish ateach (val pi in D) chk(z(pi) == x(pi) + y(pi));

        // now write back zeros to x
        x.update(Array.make[int](D));

        // ensure x is all zeros
        chk(x.sum() == 0);

        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new MiscTest1().execute();
    }
}
