/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Simple array test.
 *
 * Only uses the longhand forms such as ia.get(p) for ia[p].
 * Note: this a test only.  It is not the recommended way to
 * write x10 code.
 */
public class Array1 extends x10Test {

    public def run(): boolean = {

        val e:Region{rank==1} = 1..10;
        val r:Region{rank==2} = [e,e];
        val d = r->here;
        val ia: Array[int](d) = Array.make[int](d, (Point)=>0);

        for (p(i) in e) {
            for (q(j) in e) {
                chk(ia(i,j) == 0);
                ia(i,j) = i+j;
            }
        }

        for (val p(i,j): Point(2) in d) {
            val q1:Point(2) = [i,j];
            chk(i == q1(0));
            chk(j == q1(1));
            chk(ia(i, j) == i+j);
            chk(ia(i, j) == ia(p));
            chk(ia(q1) == ia(p));
            ia(p) = ia(p)-1;
            chk(ia(p) == i+j-1);
            chk(ia(q1) == ia(p));
        }

        return true;
    }

    public static def main(Rail[String]) = {
        new Array1().execute();
    }
}
