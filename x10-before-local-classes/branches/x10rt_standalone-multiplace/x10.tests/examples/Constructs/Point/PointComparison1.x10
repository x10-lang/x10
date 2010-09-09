// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test.

import harness.x10Test;

/**
 * Test point comparison.
 *
 * @author bdlucas 10/2008
 */

class PointComparison1 extends TestPoint {

    def comp(a: Point, b: Point) {a.rank==b.rank} {
        pr(a + "> " + b + " " + (a > b));
        pr(a + "< " + b + " " + (a < b));
        pr(a + ">=" + b + " " + (a >= b));
        pr(a + "<=" + b + " " + (a <= b));
        pr(a + "==" + b + " " + (a == b));
        pr(a + "!=" + b + " " + (a != b));
    }

    public def run() {

        val p = Point.make([1, 2, 3, 4, 5]);
        val q = Point.make([2, 3, 4, 5, 6]);
        val r = Point.make([6, 5, 4, 3, 2]);
        val s = Point.make([1, 2, 3, 4, 5]);

        comp(p,q);
        comp(p,r);
        comp(p,s);

        comp(q,p);
        comp(r,p);
        comp(s,p);

        return status();
    }

    def expected() =
        "(1,2,3,4,5)> (2,3,4,5,6) false\n"+
        "(1,2,3,4,5)< (2,3,4,5,6) true\n"+
        "(1,2,3,4,5)>=(2,3,4,5,6) false\n"+
        "(1,2,3,4,5)<=(2,3,4,5,6) true\n"+
        "(1,2,3,4,5)==(2,3,4,5,6) false\n"+
        "(1,2,3,4,5)!=(2,3,4,5,6) true\n"+
        "(1,2,3,4,5)> (6,5,4,3,2) false\n"+
        "(1,2,3,4,5)< (6,5,4,3,2) true\n"+
        "(1,2,3,4,5)>=(6,5,4,3,2) false\n"+
        "(1,2,3,4,5)<=(6,5,4,3,2) true\n"+
        "(1,2,3,4,5)==(6,5,4,3,2) false\n"+
        "(1,2,3,4,5)!=(6,5,4,3,2) true\n"+
        "(1,2,3,4,5)> (1,2,3,4,5) false\n"+
        "(1,2,3,4,5)< (1,2,3,4,5) false\n"+
        "(1,2,3,4,5)>=(1,2,3,4,5) true\n"+
        "(1,2,3,4,5)<=(1,2,3,4,5) true\n"+
        "(1,2,3,4,5)==(1,2,3,4,5) true\n"+
        "(1,2,3,4,5)!=(1,2,3,4,5) false\n"+
        "(2,3,4,5,6)> (1,2,3,4,5) true\n"+
        "(2,3,4,5,6)< (1,2,3,4,5) false\n"+
        "(2,3,4,5,6)>=(1,2,3,4,5) true\n"+
        "(2,3,4,5,6)<=(1,2,3,4,5) false\n"+
        "(2,3,4,5,6)==(1,2,3,4,5) false\n"+
        "(2,3,4,5,6)!=(1,2,3,4,5) true\n"+
        "(6,5,4,3,2)> (1,2,3,4,5) true\n"+
        "(6,5,4,3,2)< (1,2,3,4,5) false\n"+
        "(6,5,4,3,2)>=(1,2,3,4,5) true\n"+
        "(6,5,4,3,2)<=(1,2,3,4,5) false\n"+
        "(6,5,4,3,2)==(1,2,3,4,5) false\n"+
        "(6,5,4,3,2)!=(1,2,3,4,5) true\n"+
        "(1,2,3,4,5)> (1,2,3,4,5) false\n"+
        "(1,2,3,4,5)< (1,2,3,4,5) false\n"+
        "(1,2,3,4,5)>=(1,2,3,4,5) true\n"+
        "(1,2,3,4,5)<=(1,2,3,4,5) true\n"+
        "(1,2,3,4,5)==(1,2,3,4,5) true\n"+
        "(1,2,3,4,5)!=(1,2,3,4,5) false\n";

    public static def main(Rail[String]) {
        new PointComparison1().execute();
    }
    

}
