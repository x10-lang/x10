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
 * Testing that ia(p) gives an error, where ia:Array[int](1) and p:Point(2).
 */

public class ArrayAccessWithMismatchingPointRank_MustFailCompile extends x10Test {

    def a(b:int) {}

    public def run(): boolean = {

        val e = 1..10;
        val ia = Array.make[int](e->here, (Point)=>0); // will infer ia:Array[int](1)
        val p = [1,1] as Point; // will infer p:Point(2)

        a(ia(p)); // should fail at compile time because of mismatching rank.

        return true;
    }

    public static def main(Rail[String]) = {
        new ArrayAccessWithMismatchingPointRank_MustFailCompile().execute();
    }
}
