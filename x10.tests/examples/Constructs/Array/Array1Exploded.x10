/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Tests point (p[i,j]) notation.
 */

public class Array1Exploded extends x10Test {

    public def select(p(i,j): Point, (k,l): Point)=i+k;

    public def run(): boolean = {

        val d = Dist.makeConstant([1..10, 1..10] as Region, here);
        val ia = Array.make[int](d);

        for (val p(i,j): Point(2) in [1..10, 1..10] as Region) {
            chk(ia(p) == 0);
            ia(p) = i+j;
        }

        for (val p(i,j): Point(2) in d) {
            val q1(m,n)  = [i, j] as Point;
            chk(i == m);
            chk(j == n);
            chk(ia(i, j) == i+j);
            chk(ia(i, j) == ia(p));
            chk(ia(q1) == ia(p));
        }

        chk(4 == select([1, 2], [3, 4]));

        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new Array1Exploded().execute();
    }
}
