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

/**
 * Simple array test.
 *
 * Same as Array1 but shorthand forms (such as ia(p)) are used.
 */
public class Array1c extends x10Test {

    public def run(): boolean = {

        val e  = 1..10;
        val r = e*e;

        chk(r.equals((1..10)*(1..10)));

        val d = Dist.makeConstant(r, here);
        //final dist d = [1:10,1:10]->here;

        chk(d.equals(Dist.makeConstant((1..10)*(1..10), here)));
        chk(d.equals(Dist.makeConstant(e*e, here)));
        chk(d.equals(Dist.makeConstant(r, here)));

        val ia = DistArray.make[int](d, (Point)=>0);

        for (val p[i] in e) for (val q[j]  in e) {
            chk(ia(i, j) == 0);
            ia(i, j) = i+j;
        }

/*
        for (val p[i,j]:Point(2) in ia) {
            var q1[m,n]  = [i, j] as Point;
            chk(i == m);
            chk(j == n);
            chk(ia(i, j) == i+j);
            //Class cast exc. occurred at next line:
            chk(ia(i, j) == ia(p));
            chk(ia(q1) == ia(p));
            ia(p) = ia(p) - 1;
            chk(ia(p) == i + j - 1);
            chk(ia(q1) == ia(p));
        }
*/

        return true;
    }

    public static def main(var args: Array[String](1)): void = {
        new Array1c().execute();
    }
}
