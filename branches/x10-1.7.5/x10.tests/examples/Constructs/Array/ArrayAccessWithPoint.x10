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
 * Testing whether one can write ia(p) for a point with the same rank as ia.
 */

public class ArrayAccessWithPoint extends x10Test {

    def a(b:int) {}

    public def run(): boolean = {

        val e = 1..10;
        val ia = Array.make[int](e->here, (Point)=>0); // will infer ia:Array[int](1)
        val p = [1] as Point; // will infer p:Point(1).

        a(ia(p)); 

        return true;
    }

    public static def main(Rail[String]) = {
        new ArrayAccessWithPoint().execute();
    }
}
