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
 * Testing whether one can write a for (val p in ia.region) ...ia(p)... loop.
 */

public class ArrayIndexWithPoint extends x10Test {

    public def run(): boolean = {
        val e = 1..10;
        val ia = Array.make[int](e->here, (Point)=>0);
        for (val p in ia.region) // should infer p:Point(region.rank).
            chk(ia(p)==0);
        return true;
    }

    public static def main(Rail[String]) = {
        new ArrayIndexWithPoint().execute();
    }
}
