/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Test the shorthand syntax for a value array initializer.
 */

public class IntValueArrayInitializerShorthand extends x10Test {

    public def run(): boolean = {

        val d = Dist.makeConstant([1..10, 1..10], here);
        val ia = Array.make[int](d, ((i,j):Point) => i+j);

        for (p(i,j):Point(2) in [1..10, 1..10]) chk(ia(p) == i+j);

        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new IntValueArrayInitializerShorthand().execute();
    }
}
