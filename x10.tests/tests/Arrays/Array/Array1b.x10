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
 * Simple array test.
 *
 * Same as Array1 but shorthand forms (such as ia[p]) are used.
 */

public class Array1b extends x10Test {

    public def run(): boolean = {

        val e = Region.make(1,10);
        val r = e*e;

        chk(r.equals(Region.make(1..10, 1..10)));
        val d = Dist.makeConstant(Region.make(1..10, 1..10), here);

        chk(d.equals(Dist.makeConstant(Region.make(1..10, 1..10), here)));
        chk(d.equals(Dist.makeConstant(e*e, here)));
        chk(d.equals(Dist.makeConstant(r, here)));

        val ia = DistArray.make[int](d, (Point)=>0n);

        for (p[i] in e) for (q[j] in e) {
            chk(ia(i, j) == 0n);
            ia(i, j) = (i+j) as int;
        }

        for (p[i,j]  in ia.region) {
            val q1[m,n] = [i, j] as Point;
            chk(i == m);
            chk(j == n);
            chk(ia(i, j) == (i+j) as int);
            chk(ia(i, j) == ia(p));
            chk(ia(q1) == ia(p));
            ia(p) = ia(p) - 1n;
            chk(ia(p) == (i + j - 1) as int);
            chk(ia(q1) == ia(p));
        }

        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new Array1b().execute();
    }
}
