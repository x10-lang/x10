/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Test the shorthand syntax for an array initializer.
 */

public class IntArrayInitializerShorthand extends x10Test {

    public def run(): boolean = {

        val d = Dist.makeConstant([1..10, 1..10], here);
        val ia = Array.make[int](d, ((i,j):Point) => i+j);

        for (val p(i,j): Point(2) in ([1..10, 1..10] as Region))
            chk(ia(p) == i+j);

        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new IntArrayInitializerShorthand().execute();
    }
}
