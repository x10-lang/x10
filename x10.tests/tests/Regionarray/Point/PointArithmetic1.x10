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

import harness.x10Test;
import x10.regionarray.*;

/**
 * Test point arithmetic.
 *
 * @author bdlucas 10/2008
 */

class PointArithmetic1 extends TestPoint {

    public def run() {

        val p = Point.make([2, 3, 4, 5, 6]);
        val q = Point.make([6, 5, 4, 3, 2]);
        val r = Point.make([1, 2, 3, 4]);
        val c = 2;

        // points
        prPoint("p", p);
        prPoint("q", q);
        prPoint("r", r);

        // unary ops
        prPoint("+p", +p);
        prPoint("-p", -p);

        // binary ops
        prPoint("p+q", p+q);
        prPoint("p-q", p-q);
        prPoint("p*q", p*q);
        prPoint("p/q", p/q);

        // point/const binary ops
        prPoint("p+c", p+c);
        prPoint("p-c", p-c);
        prPoint("p*c", p*c);
        prPoint("p/c", p/c);

        // const/point binary ops
        prPoint("c+p", c+p);
        prPoint("c-p", c-p);
        prPoint("c*p", c*p);
        prPoint("c/p", c/p);

        return status();
    }

    def expected() =
        "p [2,3,4,5,6] sum=20\n"+
        "q [6,5,4,3,2] sum=20\n"+
        "r [1,2,3,4] sum=10\n"+
        "+p [2,3,4,5,6] sum=20\n"+
        "-p [-2,-3,-4,-5,-6] sum=-20\n"+
        "p+q [8,8,8,8,8] sum=40\n"+
        "p-q [-4,-2,0,2,4] sum=0\n"+
        "p*q [12,15,16,15,12] sum=70\n"+
        "p/q [0,0,1,1,3] sum=5\n"+
        "p+c [4,5,6,7,8] sum=30\n"+
        "p-c [0,1,2,3,4] sum=10\n"+
        "p*c [4,6,8,10,12] sum=40\n"+
        "p/c [1,1,2,2,3] sum=9\n"+
        "c+p [4,5,6,7,8] sum=30\n"+
        "c-p [0,-1,-2,-3,-4] sum=-10\n"+
        "c*p [4,6,8,10,12] sum=40\n"+
        "c/p [1,0,0,0,0] sum=1\n";

    public static def main(Rail[String]) {
        new PointArithmetic1().execute();
    }
}
